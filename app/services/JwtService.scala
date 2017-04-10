package services

import java.security.spec.{PKCS8EncodedKeySpec, RSAPublicKeySpec}
import java.security.{KeyFactory, KeyPairGenerator, PrivateKey, PublicKey}
import java.time.Instant
import java.util.Base64
import javax.inject.{Inject, Singleton}

import dao.PlatformSettingsDAO
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim, JwtJson}
import play.api.libs.json.JsObject
import sun.security.rsa.RSAPrivateCrtKeyImpl
import tables.PlatformSetting

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

/**
  * Created by richardroque on Apr/08/2017.
  */
@Singleton
class JwtService @Inject()(platformSettingsDAO: PlatformSettingsDAO)
                          (implicit ec: ExecutionContext) {

  private val PLATFORM_SETTINGS_KEY = "RSA_PRIVATE_KEY"
  private var maybePublicKey: Option[PublicKey] = None
  private var maybePrivateKey: Option[PrivateKey] = None

  def generateJwt(claim: JsObject, ttl: Long): Future[String] = {
    val claims = JwtClaim(claim.toString).expiresAt(Instant.now().plusSeconds(ttl).getEpochSecond)
    getPrivateKey.map { privateKey => Jwt.encode(claims, privateKey, JwtAlgorithm.RS512) }
  }

  def validateJwt(jwtToken: String): Future[Try[JsObject]] = {
    getPublicKey.map { publicKey => JwtJson.decodeJson(jwtToken, publicKey, Seq(JwtAlgorithm.RS512)) }
  }

  private def getPublicKey: Future[PublicKey] = {
    maybePublicKey match {
      case Some(key) => Future.successful(key)
      case _ => getPrivateKey.map { _ => maybePublicKey.get }
    }
  }

  @volatile
  private def getPrivateKey: Future[PrivateKey] = {
    maybePrivateKey match {
      case Some(key) => Future.successful(key)
      case _ =>
        (for {
          platformSettings <- platformSettingsDAO.get(PLATFORM_SETTINGS_KEY)
          newPrivateKey <- platformSettings match {
            case Some(setting) => getKeyPairFromString(setting.content)
            case _ => generateNewKeyPairAndStoreToDatabase
          }
        } yield newPrivateKey) map (keyPair => {
          maybePrivateKey = Some(keyPair._1)
          maybePublicKey = Some(keyPair._2)
          keyPair._1
        })
    }
  }

  private def getKeyPairFromString(privateKeyString: String): Future[(PrivateKey, PublicKey)] = Future {
    val privateKeyBytes = Base64.getDecoder.decode(privateKeyString)
    val spec = new PKCS8EncodedKeySpec(privateKeyBytes)
    val kf = KeyFactory.getInstance("RSA")
    val tempPrivateKey = kf.generatePrivate(spec).asInstanceOf[RSAPrivateCrtKeyImpl]
    val tempPublicKey = kf.generatePublic(new RSAPublicKeySpec(tempPrivateKey.getModulus, tempPrivateKey.getPublicExponent))
    (tempPrivateKey, tempPublicKey)
  }

  private def generateNewKeyPairAndStoreToDatabase: Future[(PrivateKey, PublicKey)] = {
    val rsaKeyGenerator = KeyPairGenerator.getInstance("RSA")
    rsaKeyGenerator.initialize(2048)
    val rsaKeyPair = rsaKeyGenerator.generateKeyPair()
    val tempPrivateKey = rsaKeyPair.getPrivate
    for {
      res <- platformSettingsDAO.insert(PlatformSetting(PLATFORM_SETTINGS_KEY, Base64.getEncoder.encodeToString(tempPrivateKey.getEncoded)))
      newKeyPair <-
      res match {
        case Success(platformSettingId) => Future.successful((tempPrivateKey, rsaKeyPair.getPublic))
        case Failure(ex) => Future.failed(ex)
      }
    } yield newKeyPair
  }

}
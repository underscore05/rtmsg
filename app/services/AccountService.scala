package services

import java.time._
import java.time.temporal._
import javax.inject.{Inject, Singleton}

import dao.AccountsDAO
import filters.UserIdentity
import org.mindrot.jbcrypt.BCrypt
import play.api.cache.{CacheApi, _}
import play.api.libs.json.{JsObject, Json}
import play.api.libs.mailer.{Email, MailerClient}
import requests.account._
import requests.account.AccountRequests._
import tables.{Account, AccountId}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

/**
  * Created by richardroque on Mar/31/2017.
  */
@Singleton
class AccountService @Inject()(accountsDAO: AccountsDAO,
                               jwtService: JwtService,
                               mailerClient: MailerClient,
                               @NamedCache("email-verification-cache") cache: CacheApi)
                              (implicit ec: ExecutionContext) {

  def register(req: RegisterAccountRequest): Future[Try[AccountId]] = {
    for {
      encryptedPassword <- Future {
        BCrypt.hashpw(req.password, BCrypt.gensalt())
      }
      accountId <- accountsDAO.insert(Account(0L, req.name, req.username, encryptedPassword, req.email))
      _ <- {
        accountId match {
          case Success(aId) =>
            val payload = VerifyAccountEmailRequest(verifyEmail = true, aId)
            for {
              verificationToken <- jwtService.generateJwt(Json.toJson(payload).as[JsObject], 300)
              _ <- Future {
                val body = s"You verification token is $verificationToken"
                val email = Email("Verify Email", "dev.rroque@gmail.com", Seq(req.email), Some(body))
                mailerClient.send(email)
              }
            } yield verificationToken
          case _ => Future.successful("")
        }
      }
    } yield accountId
  }

  def resendVerificationEmail(req: ResendEmailVerificationRequest): Future[Try[Boolean]] = {
    cache.get[Instant](req.email) match {
      case Some(lastEmailVerification) => {
        val windowTime = ChronoUnit.SECONDS.between(Instant.now, lastEmailVerification) + 60
        Future.successful(Failure(new Exception(s"Please resend email verification after $windowTime seconds.")))
      }
      case _ =>
        cache.set(req.email, Instant.now, 60.seconds)
        for {
          maybeAccount <- accountsDAO.getByEmail(req.email)
          result <- {
            maybeAccount match {
              case Some(account) if !account.isEmailVerified => {
                val payload = VerifyAccountEmailRequest(verifyEmail = true, account.accountId)
                for {
                  verificationToken <- jwtService.generateJwt(Json.toJson(payload).as[JsObject], 300)
                  _ <- Future {
                    val body = s"You verification token is $verificationToken"
                    val email = Email("Verify Email", "dev.rroque@gmail.com", Seq(req.email), Some(body))
                    mailerClient.send(email)
                  }
                } yield Success(true)
              }
              case Some(account) => Future.successful(Failure(new Exception("Already verified.")))
              case _ => Future.successful(Success(true))
            }
          }
        } yield result
    }
  }

  def login(req: LoginAccountRequest): Future[Try[String]] = {
    for {
      someAccount <- accountsDAO.getByUsername(req.username)
      someAccountToken <- someAccount match {
        case Some(account) if BCrypt.checkpw(req.password, account.password) => {
          if (account.isActive) {
            if (account.isEmailVerified) {
              jwtService.generateJwt(Json.toJson(UserIdentity(account.accountId, req.username)).as[JsObject], 600).map { s => Success(s) }
            } else {
              Future.successful(Failure(new Exception("Please verify your email before logging-in")))
            }
          } else {
            Future.successful(Failure(new Exception("It looks like your account has been deactivated. Please contact your administrator.")))
          }
        }
        case _ => Future.successful(Failure(new Exception("Incorrect username and password")))
      }
    } yield someAccountToken
  }

  def getUser(id: String): Option[(String, String)] = {
    AccountService.getUser(id)
  }

  def verifyAccountEmail(token: String): Future[Try[Int]] = {
    for {
      validationResult <- jwtService.validateJwt(token)
      updateResult <- {
        validationResult match {
          case Success(jsObj) =>
            jsObj.validate[VerifyAccountEmailRequest].map { req =>
              accountsDAO.updateEmailVerification(req.accountId, isEmailVerified = true)
            } recoverTotal { parsingError =>
              Future.successful(Failure(new Exception))
            }
          case Failure(ex) => Future.successful(Failure(ex))
        }
      }
    } yield updateResult
  }

  def refreshToken(req: UserIdentity): Future[Try[String]] = {
    for {
      someAccount <- accountsDAO.get(req.accountId)
      someAccountToken <- someAccount match {
        case Some(account) =>
          if (account.isActive) {
            jwtService.generateJwt(Json.toJson(UserIdentity(req.accountId, req.username)).as[JsObject], 600).map { s => Success(s) }
          } else {
            Future.successful(Failure(new Exception("It looks like your account has been deactivated. Please contact your administrator.")))
          }
        case _ =>
          // Note: This shouldn't happen except that the user has been deleted from our database.
          Future.successful(Failure(new Exception("User no longer exists")))
      }
    } yield someAccountToken
  }
}


object AccountService {
  private val users = Map(
    "0001" -> ("Richard Neil Roque", "21584502")
  )

  private def getUser(id: String): Option[(String, String)] = users.get(id)

}



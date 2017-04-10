package filters

import javax.inject.Inject

import com.google.inject.Singleton
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc.Security.AuthenticatedBuilder
import services.JwtService
import tables.{AccountId, ShortcodeId}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
  * Created by richardroque on Mar/31/2017.
  */
@Singleton
class SecuredShortcodeAction @Inject()(jwtService: JwtService)(implicit ec: ExecutionContext) extends AuthenticatedBuilder(
  headers => {
    def getToken: Option[String] = headers.getQueryString("token") match {
      case Some(token) => Some(token)
      case _ =>
        val bearerTokenPattern ="""(?i)^\s*Bearer\s+(\S+?)\s*$""".r
        headers.headers.get("Authorization").flatMap(s => s match {
          case bearerTokenPattern(token) => Some(token)
          case _ => None
        })
    }
    val getCredentials: Future[Option[ShortcodeIdentity]] = for {
      validationResult <- getToken match {
        case Some(token) => jwtService.validateJwt(token)
        case _ => Future.successful(Failure(new Exception))
      }
      credentials <- Future {
        validationResult match {
          case Success(jsObj) => {
            import ShortcodeIdentity.shortcodeIdentityJsonFormatter
            jsObj.validate[ShortcodeIdentity](shortcodeIdentityJsonFormatter)
              .map { newObj => Some(newObj) }
              .recoverTotal { parsingError => None }
          }
          case Failure(ex) => None
        }
      }
    } yield credentials
    Await.result(getCredentials, 10.seconds)
  },
  headers => Unauthorized
)

case class ShortcodeIdentity(shortcodeId: ShortcodeId, accountId: AccountId)

object ShortcodeIdentity {
  implicit val shortcodeIdentityJsonFormatter = Json.format[ShortcodeIdentity]
}


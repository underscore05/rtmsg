package filters

import javax.inject.Inject

import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc.Security.AuthenticatedBuilder
import services.JwtService
import tables._

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
  * Created by richardroque on Mar/31/2017.
  */
class SecureUserAction @Inject()(jwtService: JwtService)
                                (implicit val ec: ExecutionContext) extends AuthenticatedBuilder(
  headers => {
    def getToken = headers.getQueryString("token") match {
      case Some(token) => Some(token)
      case _ =>
        val bearerTokenPattern ="""(?i)^\s*Bearer\s+(\S+?)\s*$""".r
        def getBearerToken(str: String) = str match {
          case bearerTokenPattern(token) => Some(token)
          case _ => None
        }
        headers.headers.get("Authorization").flatMap(getBearerToken)
    }

    val getUser: Future[Option[UserIdentity]] = for {
      validationResult <- getToken match {
        case Some(token) => jwtService.validateJwt(token)
        case _ => Future.successful(Failure(new Exception))
      }
      user <- {
        val y = Future {
          validationResult match {
            case Success(jsObj) => {
              implicit val impReader = Json.reads[UserIdentity]
              jsObj.validate[UserIdentity](impReader).map {
                newObj => Some(newObj)
              } recoverTotal { parsingError => None }
            }
            case Failure(ex) => None
          }
        }
        y
      }
    } yield user
    Await.result(getUser, 10.seconds)
  },
  headers => Unauthorized
)

case class UserIdentity(accountId: AccountId, username: String)

object UserIdentity {
  implicit val userIdentityJsonFormatter = Json.format[UserIdentity]
}
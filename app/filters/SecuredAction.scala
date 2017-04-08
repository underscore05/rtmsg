package filters

import javax.inject.Inject

import com.google.inject.Singleton
import play.api.mvc.Results._
import play.api.mvc.Security.AuthenticatedBuilder
import requests.accounts.VerifyAccountEmailRequest._
import services.JwtService

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
  * Created by richardroque on Mar/31/2017.
  */
@Singleton
class SecuredAction @Inject()(jwtService: JwtService)
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

    val getUser: Future[Option[ClientApiKey]] = for {
      validationResult <- getToken match {
        case Some(token) => jwtService.validateJwt(token)
        case _ => Future.successful(Failure(new Exception))
      }
      user <- {
        val y = Future {
          validationResult match {
            case Success(jsObj) => jsObj.validate[ClientApiKey].map {
              newObj => Some(newObj)
            } recoverTotal { parsingError => None }
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
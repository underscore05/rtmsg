package controllers

import javax.inject._

import filters.SecureUserAction
import play.api.http.HttpErrorHandler
import play.api.libs.json.Json
import play.api.mvc._
import requests.account._
import requests.account.AccountRequests._
import services.AccountService

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class AccountController @Inject()(securedAction: SecureUserAction,
                                  accountService: AccountService,
                                  httpErrorHandler: HttpErrorHandler)
                                 (implicit ec: ExecutionContext) extends Controller {

  def register = Action.async(parse.json[RegisterAccountRequest]) { request =>
    accountService.register(request.body).flatMap {
      case Success(accountId) => Future.successful(Created)
      case Failure(ex: Exception) => httpErrorHandler.onClientError(request, 400, ex.getLocalizedMessage)
    }
  }

  def verifyEmail(token: String) = Action.async { request =>
    accountService.verifyAccountEmail(token) flatMap {
      case Success(_) => Future.successful(Ok)
      case Failure(ex: Exception) => httpErrorHandler.onClientError(request, 400, ex.getLocalizedMessage)
    }
  }

  /**
    * This function return 200 Ok even if the email doesn't exists.
    * It returns 400 Bad Request if the user is already verified.
    *
    * @return
    */
  def resendVerificationEmail = Action.async(parse.json[ResendEmailVerificationRequest]) { request =>
    accountService.resendVerificationEmail(request.body) flatMap {
      case Success(_) => Future.successful(Ok)
      case Failure(ex: Exception) => httpErrorHandler.onClientError(request, 400, ex.getLocalizedMessage)
    }
  }

  def login = Action.async(parse.json[LoginAccountRequest]) { request =>
    accountService.login(request.body) flatMap {
      case Success(token) => Future.successful(Ok(Json.obj("token" -> token)))
      case Failure(ex: Exception) => httpErrorHandler.onClientError(request, 400, ex.getLocalizedMessage)
    }
  }

  def refreshToken = securedAction.async { request =>
    accountService.refreshToken(request.user) flatMap {
      case Success(token) => Future.successful(Ok(Json.obj("token" -> token)))
      case Failure(ex: Exception) => httpErrorHandler.onClientError(request, 400, ex.getLocalizedMessage)
    }
  }
}

package controllers

import javax.inject._

import filters.SecuredAction
import play.api.Logger
import play.api.http.HttpErrorHandler
import play.api.mvc._
import requests.accounts.{RegisterAccountRequest, ResendEmailVerificationRequest}
import requests.accounts.VerifyAccountEmailRequest._
import services.AccountService

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class AccountController @Inject()(securedAction: SecuredAction,
                                  accountService: AccountService,
                                  httpErrorHandler: HttpErrorHandler)
                                 (implicit ec: ExecutionContext) extends Controller {

  def register = Action.async(parse.json[RegisterAccountRequest]) { request =>
    accountService.register(request.body).flatMap {
      case Success(accountId) => Future.successful(Created)
      case Failure(ex) => ex match {
        case _ => httpErrorHandler.onClientError(request, 400, ex.getLocalizedMessage)
      }
    }
  }

  def verifyEmail(token: String) = Action.async { request =>
    accountService.verifyAccountEmail(token) flatMap {
      case Success(_) => Future.successful(Ok)
      case Failure(ex) => ex match {
        case _ => httpErrorHandler.onClientError(request, 400, ex.getLocalizedMessage)
      }
    }
  }

  def resendVerificationEmail = Action.async(parse.json[ResendEmailVerificationRequest]) { request =>
    accountService.resendVerificationEmail(request.body) flatMap {
      case Success(_) => Future.successful(Ok)
      case Failure(ex) => ex match {
        case _ => httpErrorHandler.onClientError(request, 400, ex.getLocalizedMessage)
      }
    }
  }

  def login = Action.async { request =>
    ???
  }

  def refreshToken = securedAction.async { request =>
    ???
  }
}

package controllers

import javax.inject._

import filters.SecureUserAction
import play.api.http.HttpErrorHandler
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class ShortcodeController @Inject()(securedAction: SecureUserAction,
                                    httpErrorHandler: HttpErrorHandler)
                                   (implicit ec: ExecutionContext) extends Controller {

  def create = securedAction.async(parse.json) {
    ???
  }
}

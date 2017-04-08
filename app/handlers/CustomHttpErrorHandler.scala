package handlers

import javax.inject.Singleton

import play.api.http.HttpErrorHandler
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc.{RequestHeader, Result}

import scala.concurrent.Future

/**
  * Created by richardroque on Mar/31/2017.
  */
@Singleton
class CustomHttpErrorHandler extends HttpErrorHandler {

  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    Future.successful(Status(statusCode)(Json.obj("code" -> statusCode, "message" -> message)))
  }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    Future.successful {
      exception.printStackTrace()
      InternalServerError(Json.obj("code" -> 500, "message" -> exception.getMessage))
    }
  }
}

package controllers

import javax.inject._

import filters.SecuredWebHookAction
import play.api.Logger
import play.api.mvc._
import requests.globelabs.{GlobelabsIncomingSmsRequest, GlobelabsOptoutRequest}
import services.GlobelabsSmsService
import tables.AccessToken

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GlobelabsSmsController @Inject()(securedAction: SecuredWebHookAction,
                                       globelabsSmsService: GlobelabsSmsService)
                                      (implicit ec: ExecutionContext) extends Controller {

  def optOut = securedAction.async(parse.json[GlobelabsOptoutRequest]) { request =>
    val clientApiKey = request.user
    val optOptoutRequest = request.body
    globelabsSmsService.optOut(clientApiKey.shortcode, optOptoutRequest.unsubscribed.subscriber_number).map { success =>
      Ok
    }
  }

  def optIn(msisdn: String, accessToken: AccessToken) = securedAction.async { request =>
    val clientApiKey = request.user
    globelabsSmsService.optIn(clientApiKey.shortcode, msisdn, accessToken).map { subscriberId =>
      Ok
    }
  }

  def deliverSms = securedAction.async(parse.json[GlobelabsIncomingSmsRequest]) { request =>
    Future.successful {
      Logger.info("Deliver POST:" + request.body)
      Ok
    }
  }

  def sendSms(msisdn: String, message: String) = securedAction.async { request =>
    val clientApiKey = request.user
    globelabsSmsService.sendMessage(clientApiKey.shortcode, msisdn, message).map {
      case true => Ok
      case false => NotFound
    }
  }
}

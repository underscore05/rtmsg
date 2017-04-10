package controllers

import javax.inject._

import filters.SecuredShortcodeAction
import play.api.Logger
import play.api.mvc._
import requests.globelabs.{GlobelabsIncomingSmsRequest, GlobelabsOptoutRequest}
import services.GlobelabsSmsService
import tables.AccessToken

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GlobelabsSmsController @Inject()(shortcodeAction: SecuredShortcodeAction,
                                       globelabsSmsService: GlobelabsSmsService)
                                      (implicit ec: ExecutionContext) extends Controller {

  def optOut = shortcodeAction.async(parse.json[GlobelabsOptoutRequest]) { request =>
    val userIdentity = request.user
    val optOptoutRequest = request.body
    globelabsSmsService.optOut(userIdentity.shortcodeId, optOptoutRequest.unsubscribed.subscriber_number).map { success =>
      Ok
    }
  }

  def optIn(msisdn: String, accessToken: AccessToken) = shortcodeAction.async { request =>
    val userIdentity = request.user
    globelabsSmsService.optIn(userIdentity.shortcodeId, msisdn, accessToken).map { subscriberId =>
      Ok
    }
  }

  def deliverSms = shortcodeAction.async(parse.json[GlobelabsIncomingSmsRequest]) { request =>
    Future.successful {
      Logger.info("Deliver POST:" + request.body)
      Ok
    }
  }

  def sendSms(msisdn: String, message: String) = shortcodeAction.async { request =>
    val shortcodeIdentity = request.user
    globelabsSmsService.sendMessage(shortcodeIdentity.shortcodeId, msisdn, message).map {
      case true => Ok
      case false => NotFound
    }
  }
}

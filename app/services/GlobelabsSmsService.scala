package services

import java.util.UUID
import javax.inject.{Inject, Singleton}

import dao.{GlobelabsSubscribersDAO, SubscribersDAO}
import play.api.Logger
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import requests.globelabs.{GlobelabsOutgoingSmsMessageRequest, GlobelabsOutgoingSmsPayloadRequest, GlobelabsOutgoingSmsRequest}
import tables.{AccessToken, GlobelabsSubscriber, Subscriber}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by richardroque on Apr/01/2017.
  */
@Singleton
class GlobelabsSmsService @Inject()(globelabsSubscriberDAO: GlobelabsSubscribersDAO,
                                    subscriberDAO: SubscribersDAO,
                                    ws: WSClient
                                   )(implicit ec: ExecutionContext) {

  def optIn(shortcode: String, msisdn: String, accessToken: AccessToken) = {
    val subscriber = Subscriber(0L, msisdn, shortcode)
    val globelabsSubscriber = GlobelabsSubscriber(subscriber, accessToken)
    globelabsSubscriberDAO.insertWithSubscriber(globelabsSubscriber)
  }

  def optOut(shortcode: String, msisdn: String) = {
    // No need to explicitly call globelabsSubscriberDAO.deleteByMsisdnAndShortcode
    // since deleting subscriber will cascade it to globelabs_subscriber
    subscriberDAO.deleteByMsisdnAndShortcode(msisdn, shortcode)
  }

  def sendMessage(shortcode: String, msisdn: String, message: String) = {
    globelabsSubscriberDAO.getByShortcodeAndMsisdn(shortcode, msisdn) flatMap {
      case Some(globelabsSubscriber) => callGlobelabsSmsAPI(globelabsSubscriber, message).map { _ => true }
      case _ => Future.successful(false)
    }
  }

  def callGlobelabsSmsAPI(globelabsSubscriber: GlobelabsSubscriber, message: String) = {
    val globelabsUrl = "https://devapi.globelabs.com.ph"
    val subscriber = globelabsSubscriber.subscriber
    val payload = GlobelabsOutgoingSmsRequest(outboundSMSMessageRequest =
      GlobelabsOutgoingSmsPayloadRequest(
        clientCorrelator = UUID.randomUUID().toString,
        senderAddress = subscriber.shortcode,
        outboundSMSTextMessage = GlobelabsOutgoingSmsMessageRequest(message = message),
        address = subscriber.msisdn
      )
    )
    val url = s"$globelabsUrl/smsmessaging/v1/outbound/${subscriber.shortcode}/requests?access_token=${globelabsSubscriber.accessToken}"
    ws.url(url).post(Json.toJson(payload)).map { res =>
      Logger.info("URL:" + url)
      Logger.info("Response:" + res)
      res
    }
  }
}

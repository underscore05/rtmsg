package requests.globelabs

import play.api.libs.json.Json

/**
  * Created by richardroque on Mar/31/2017.
  */
case class GlobelabsOutgoingSmsRequest(outboundSMSMessageRequest: GlobelabsOutgoingSmsPayloadRequest)


case class GlobelabsOutgoingSmsPayloadRequest(
                                               clientCorrelator: String,
                                               senderAddress: String,
                                               outboundSMSTextMessage: GlobelabsOutgoingSmsMessageRequest,
                                               address: String
                                       )

case class GlobelabsOutgoingSmsMessageRequest(
                                     message: String
                                   )

object GlobelabsOutgoingSmsRequest {
  implicit val outgoingSmsMessageRequestJsonReader = Json.format[GlobelabsOutgoingSmsMessageRequest]
  implicit val outgoingSmsPayloadRequestJsonReader = Json.format[GlobelabsOutgoingSmsPayloadRequest]
  implicit val outgoingSmsRequestJsonReader = Json.format[GlobelabsOutgoingSmsRequest]
}
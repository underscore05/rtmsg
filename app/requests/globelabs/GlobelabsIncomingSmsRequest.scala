package requests.globelabs

import play.api.libs.json.Json

/**
  * Created by richardroque on Mar/31/2017.
  */
case class GlobelabsIncomingSmsRequest(inboundSMSMessageList: GlobelabsInboundSmsMessageListRequest)


case class GlobelabsInboundSmsMessageListRequest(
                                                  inboundSMSMessage: Seq[GlobelabsInboundSmsMessageRequest],
                                                  numberOfMessagesInThisBatch: Int,
                                                  totalNumberOfPendingMessages: Int
                                       )

case class GlobelabsInboundSmsMessageRequest(
                                     messageId: String,
                                     message: String,
                                     senderAddress: String,
                                     destinationAddress: String,
                                     multipartRefId: Option[String],
                                     multipartSeqNum: Option[String]
                                   )

object GlobelabsIncomingSmsRequest {
  implicit val inboundSmsMessageRequestJsonReader = Json.format[GlobelabsInboundSmsMessageRequest]
  implicit val inboundSmsMessageListRequestJsonReader = Json.format[GlobelabsInboundSmsMessageListRequest]
  implicit val incomingSmsRequestJsonReader = Json.format[GlobelabsIncomingSmsRequest]
}
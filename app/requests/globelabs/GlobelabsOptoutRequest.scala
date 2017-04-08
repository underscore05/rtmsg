package requests.globelabs

import play.api.libs.json.Json

/**
  * Created by richardroque on Mar/31/2017.
  */
case class GlobelabsOptoutRequest(unsubscribed: GlobelabsOptoutPayloadRequest)

case class GlobelabsOptoutPayloadRequest(
                                          subscriber_number: String,
                                          shortcode: String
                                        )

object GlobelabsOptoutRequest {
  implicit val globelabsOptoutPayloadRequestJsonReader = Json.format[GlobelabsOptoutPayloadRequest]
  implicit val globelabsOptoutRequestJsonReader = Json.format[GlobelabsOptoutRequest]
}
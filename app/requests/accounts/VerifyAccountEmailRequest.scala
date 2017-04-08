package requests.accounts

import play.api.libs.json.Json
import tables.AccountId

/**
  * Created by richardroque on Apr/08/2017.
  */
case class VerifyAccountEmailRequest(verifyEmail: Boolean, accountId: AccountId, email: String)

object VerifyAccountEmailRequest {
  implicit val verifyAccountEmailRequestJsonFormatter = Json.format[VerifyAccountEmailRequest]
}

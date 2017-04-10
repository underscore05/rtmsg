package requests.account

import play.api.libs.json.Json
import tables._

/**
  * Created by richardroque on Apr/05/2017.
  */
object AccountRequests {
  implicit val loginAccountRequestJsonFormatter = Json.format[LoginAccountRequest]
  implicit val registerAccountRequestJsonFormatter = Json.format[RegisterAccountRequest]
  implicit val resendEmailVerificationRequestJsonFormatter = Json.format[ResendEmailVerificationRequest]
  implicit val verifyAccountEmailRequestJsonFormatter = Json.format[VerifyAccountEmailRequest]
}

case class LoginAccountRequest(username: String, password: String)

case class RegisterAccountRequest(name: String, username: String, password: String, email: String)

case class ResendEmailVerificationRequest(email: String)

case class VerifyAccountEmailRequest(verifyEmail: Boolean, accountId: AccountId)

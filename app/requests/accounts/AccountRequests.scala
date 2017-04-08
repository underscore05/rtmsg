package requests.accounts

import play.api.libs.json.Json

/**
  * Created by richardroque on Apr/05/2017.
  */
case class RegisterAccountRequest(name: String, username: String, password: String, email: String)

case class LoginAccountRequest(username: String, password: String)

case class ResendEmailVerificationRequest(email: String)

object RegisterAccountRequest {
  implicit val registerAccountRequestJsonFormatter = Json.format[RegisterAccountRequest]
}

object LoginAccountRequest {
  implicit val loginAccountRequestJsonFormatter = Json.format[LoginAccountRequest]
}

object ResendEmailVerificationRequest {
  implicit val resendEmailVerificationRequestJsonFormatter = Json.format[ResendEmailVerificationRequest]
}

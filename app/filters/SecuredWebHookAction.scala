package filters

import javax.inject.Inject

import com.google.inject.Singleton
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc.Security.AuthenticatedBuilder
import services.AccountService

/**
  * Created by richardroque on Mar/31/2017.
  */
@Singleton
class SecuredWebHookAction @Inject()(accountService: AccountService) extends AuthenticatedBuilder(
  headers => {
    headers.getQueryString("api_key") flatMap { apiKey =>
      accountService.getUser(apiKey) map {
        (ClientApiKey.apply _).tupled(_)
      }
    }
  },
  headers => Unauthorized
)

case class ClientApiKey(username: String, shortcode: String)
object ClientApiKey {
  implicit val clientApiKeyJsonFormatter = Json.format[ClientApiKey]
}
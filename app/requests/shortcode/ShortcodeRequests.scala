package requests.shortcode

import play.api.libs.json.Json
import tables.{ShortcodeId, ShortcodeTypes}
import tables.ShortcodeTypes.ShortcodeType
import utils.EnumUtils

/**
  * Created by richardroque on Apr/09/2017.
  */
object ShortcodeRequests {
  implicit val shortcodeTypeJsonFormat = EnumUtils.enumFormat(ShortcodeTypes)
  implicit val createShortcodeRequestJsonFormat = Json.format[CreateShortcodeRequest]
}

case class CreateShortcodeRequest(shortcodeId: ShortcodeId, shortcodeType: ShortcodeType, shortcodeAppId: String, shortcodeAppSecret: String)

package tables

import java.time.Instant

import drivers.DatabaseDriver.api._
import tables.ShortcodeTypes.ShortcodeType

/**
  * Created by richardroque on Apr/01/2017.
  */
class ShortcodesTable(tag: Tag) extends Table[Shortcode](tag, "shortcodes") {

  override def * = (shortcodeId, accountId, shortcodeType, shortcodeAppId, shortcodeAppSecret, isActive, createdAt) <> (Shortcode.tupled, Shortcode.unapply)

  def shortcodeId = column[ShortcodeId]("shortcode_id", O.PrimaryKey)

  def accountId = column[AccountId]("account_id")

  def shortcodeType = column[ShortcodeType]("shortcode_type")

  def shortcodeAppId = column[String]("shortcode_app_id")

  def shortcodeAppSecret = column[String]("shortcode_app_secret")

  def isActive = column[Boolean]("is_active")

  def createdAt = column[Instant]("created_at")

}

case class Shortcode(shortcodeId: ShortcodeId,
                     accountId: AccountId,
                     shortcodeType: ShortcodeType,
                     shortcodeAppId: String,
                     shortcodeAppSecret: String,
                     isActive: Boolean = true,
                     createdAt: Instant = Instant.now
                    )

object ShortcodeTypes extends Enumeration {
  type ShortcodeType = Value
  val GLOBELABS, CHIKKA = Value
}
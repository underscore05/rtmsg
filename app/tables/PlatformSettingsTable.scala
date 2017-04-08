package tables

import drivers.DatabaseDriver.api._

/**
  * Created by richardroque on Apr/01/2017.
  */
class PlatformSettingsTable(tag: Tag) extends Table[PlatformSetting](tag, "platform_settings") {

  override def * = (platformSettingId, content) <> (PlatformSetting.tupled, PlatformSetting.unapply)

  def platformSettingId = column[PlatformSettingId]("platform_setting_id", O.PrimaryKey)

  def content = column[String]("content")

}

case class PlatformSetting(platformSettingId: PlatformSettingId, content: String)
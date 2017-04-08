package dao

import javax.inject.Inject

import drivers.DatabaseDriver.api._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import tables._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

/**
  * Created by richardroque on Apr/05/2017.
  */
class PlatformSettingsDAO @Inject()(databaseConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val db = databaseConfigProvider.get[JdbcProfile].db
  val platformSettingsQuery = TableQuery[PlatformSettingsTable]

  def insert(data: PlatformSetting): Future[Try[PlatformSettingId]] = {
    db.run(platformSettingsQuery += data)
      .map(_ => Success(data.platformSettingId))
      .recover {
        case e =>
          Logger.error("Exception" + e)
          Failure(e)
      }
  }

  def get(platformSettingId: PlatformSettingId): Future[Option[PlatformSetting]] = {
    db.run(platformSettingsQuery.filter(_.platformSettingId===platformSettingId).result.headOption)
  }

}


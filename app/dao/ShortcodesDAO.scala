package dao

import javax.inject.Inject

import drivers.DatabaseDriver.api._
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import tables._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/**
  * Created by richardroque on Apr/05/2017.
  */
class ShortcodesDAO @Inject()(databaseConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val db = databaseConfigProvider.get[JdbcProfile].db
  val shortcodeQuery = TableQuery[ShortcodesTable]

  def insert(data: Shortcode): Future[Try[ShortcodeId]] = {
    db.run(((shortcodeQuery returning shortcodeQuery.map(_.shortcodeId)) += data).asTry)
  }

  def get(shortcodeId: ShortcodeId): Future[Option[Shortcode]] = {
    db.run(shortcodeQuery.filter(_.shortcodeId === shortcodeId).result.headOption)
  }

}


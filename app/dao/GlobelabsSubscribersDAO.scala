package dao

import javax.inject.{Inject, Singleton}

import drivers.DatabaseDriver.api._
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import tables.{GlobelabsSubscriber, _}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by richardroque on Apr/01/2017.
  */
@Singleton
class GlobelabsSubscribersDAO @Inject()(databaseConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val db = databaseConfigProvider.get[JdbcProfile].db
  val globelabsSubscriberQuery = TableQuery[GlobelabsSubscribersTable]
  val subscriberQuery = TableQuery[SubscriberTable]

  def insert(data: GlobelabsSubscriber): Future[Option[SubscriberId]] = {
    db.run((globelabsSubscriberQuery += data).map(_ => data.subscriber.subscriberId).asTry).map(_.toOption)
  }

  def get(subscriberId: SubscriberId): Future[Option[GlobelabsSubscriber]] = {
    db.run(globelabsSubscriberQuery.filter(_.subscriberId === subscriberId).result.headOption)
  }

  def delete(subscriberId: SubscriberId): Future[Boolean] = {
    db.run(globelabsSubscriberQuery.filter(_.subscriberId === subscriberId).delete).map(_ => true)
  }

  def getByShortcodeAndMsisdn(shortcode: String, msisdn: String): Future[Option[GlobelabsSubscriber]] = {
    db.run(globelabsSubscriberQuery.filter(s => s.shortcode === shortcode && s.msisdn === msisdn).result.headOption)
  }

  def insertWithSubscriber(data: GlobelabsSubscriber): Future[Option[SubscriberId]] = {
    val insertQuery = for {
      newSubscriberId <- (subscriberQuery returning subscriberQuery.map(_.subscriberId)) += data.subscriber
      _ <- {
        val newSubscriber = data.subscriber.copy(subscriberId = newSubscriberId)
        globelabsSubscriberQuery += data.copy(subscriber = newSubscriber)
      }
    } yield newSubscriberId
    db.run(insertQuery.transactionally.asTry).map(_.toOption)
  }

}


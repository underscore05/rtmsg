package dao

import javax.inject.Inject

import drivers.DatabaseDriver.api._
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import tables.{Subscriber, SubscriberId, SubscriberTable}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by richardroque on Apr/01/2017.
  */
class SubscribersDAO @Inject()(databaseConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val db = databaseConfigProvider.get[JdbcProfile].db
  val subscriberQuery = TableQuery[SubscriberTable]

  def insert(data: Subscriber): Future[Option[SubscriberId]] = {
    db.run(((subscriberQuery returning subscriberQuery.map(_.subscriberId)) += data).asTry).map(_.toOption)
  }

  def get(subscriberId: SubscriberId): Future[Option[Subscriber]] = {
    db.run(subscriberQuery.filter(_.subscriberId === subscriberId).result.headOption)
  }

  def delete(subscriberId: SubscriberId): Future[Boolean] = {
    ???
  }

  def deleteByMsisdnAndShortcode(msisdn: String, shortcode: String): Future[Boolean] ={
    val deletedSubscriberQuery = subscriberQuery.filter(s => {
      s.msisdn === msisdn && s.shortcode === shortcode
    })
    db.run(deletedSubscriberQuery.delete).map(_ > 0)
  }
}


package tables

import java.time.Instant

import drivers.DatabaseDriver.api._

/**
  * Created by richardroque on Apr/01/2017.
  */
class GlobelabsSubscribersTable(tag: Tag) extends Table[GlobelabsSubscriber](tag, "globelabs_subscribers") {

  override def * = (subscriberShape, accessToken) <> (GlobelabsSubscriber.tupled, GlobelabsSubscriber.unapply)

  def subscriberShape = (subscriberId, msisdn, shortcode, createdAt) <> (Subscriber.tupled, Subscriber.unapply)

  def subscriberId = column[SubscriberId]("subscriber_id", O.PrimaryKey)

  def msisdn = column[String]("msisdn")

  def shortcode = column[String]("shortcode")

  def createdAt = column[Instant]("created_at")

  def accessToken = column[AccessToken]("access_token")

}

case class GlobelabsSubscriber(subscriber: Subscriber, accessToken: AccessToken)
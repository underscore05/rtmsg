package tables

import java.time.Instant

import drivers.DatabaseDriver.api._

/**
  * Created by richardroque on Apr/01/2017.
  */
class SubscriberTable(tag: Tag) extends Table[Subscriber](tag, "subscribers") {

  override def * = (subscriberId, msisdn, shortcode, createdAt) <> (Subscriber.tupled, Subscriber.unapply)

  def subscriberId = column[SubscriberId]("subscriber_id", O.PrimaryKey, O.AutoInc)

  def msisdn = column[String]("msisdn")

  def shortcode = column[String]("shortcode")

  def createdAt = column[Instant]("created_at")

}

case class Subscriber(subscriberId: SubscriberId,
                      msisdn: String,
                      shortcode: String,
                      createdAt: Instant = Instant.now)
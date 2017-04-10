package tables

import java.time.Instant

import drivers.DatabaseDriver.api._

/**
  * Created by richardroque on Apr/01/2017.
  */
class SubscriberTable(tag: Tag) extends Table[Subscriber](tag, "subscribers") {

  override def * = (subscriberId, msisdn, shortcodeId, createdAt) <> (Subscriber.tupled, Subscriber.unapply)

  def subscriberId = column[SubscriberId]("subscriber_id", O.PrimaryKey, O.AutoInc)

  def msisdn = column[String]("msisdn")

  def shortcodeId = column[String]("shortcode_id")

  def createdAt = column[Instant]("created_at")

}

case class Subscriber(subscriberId: SubscriberId,
                      msisdn: String,
                      shortcodeId: ShortcodeId,
                      createdAt: Instant = Instant.now)
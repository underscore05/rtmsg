package tables

import java.time.Instant

import drivers.DatabaseDriver.api._

/**
  * Created by richardroque on Apr/01/2017.
  */
class AccountsTable(tag: Tag) extends Table[Account](tag, "accounts") {

  override def * = (accountId, name, username, password, email, isEmailVerified, createdAt) <> (Account.tupled, Account.unapply)

  def accountId = column[AccountId]("account_id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def username = column[String]("username")

  def password = column[String]("password")

  def createdAt = column[Instant]("created_at")

  def email = column[String]("email")

  def isEmailVerified = column[Boolean]("is_email_verified")

}

case class Account(accountId: AccountId,
                   name: String,
                   username: String,
                   password: String,
                   email: String,
                   isEmailVerified: Boolean = false,
                   createdAt: Instant = Instant.now)
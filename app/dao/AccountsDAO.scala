package dao

import java.sql.SQLException
import javax.inject.Inject

import drivers.DatabaseDriver.api._
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import tables.{Account, AccountId, AccountsTable}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

/**
  * Created by richardroque on Apr/05/2017.
  */
class AccountsDAO @Inject()(databaseConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val db = databaseConfigProvider.get[JdbcProfile].db
  val accountQuery = TableQuery[AccountsTable]

  def insert(data: Account): Future[Try[AccountId]] = {
    db.run(((accountQuery returning accountQuery.map(_.accountId)) += data).asTry)
  }

  def updateEmailVerification(accountId: AccountId, email: String, isEmailVerified: Boolean): Future[Try[Int]] = {
    db.run {
      accountQuery
        .filter(a => a.accountId === accountId && a.email === email)
        .map(_.isEmailVerified)
        .update(isEmailVerified).asTry
    }.map {
      case Success(affectedCount) => if (affectedCount > 0) Success(affectedCount) else Failure(new SQLException("Nothing to update"))
      case Failure(ex) => Failure(ex)
    }
  }

  def findByUsername(username: String): Future[Option[Account]] = {
    db.run(accountQuery.filter(_.username === username).result.headOption)
  }

  def findByEmail(email: String): Future[Option[Account]] = {
    db.run(accountQuery.filter(_.email === email).result.headOption)
  }

}


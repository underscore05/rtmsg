package dao

import java.sql.SQLException
import javax.inject.{Inject, Singleton}

import drivers.DatabaseDriver.api._
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import tables.{Account, AccountId, AccountsTable}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

/**
  * Created by richardroque on Apr/05/2017.
  */
@Singleton
class AccountsDAO @Inject()(databaseConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val db = databaseConfigProvider.get[JdbcProfile].db
  val accountQuery = TableQuery[AccountsTable]

  def insert(data: Account): Future[Try[AccountId]] = {
    db.run(((accountQuery returning accountQuery.map(_.accountId)) += data).asTry)
  }

  def updateEmailVerification(accountId: AccountId, isEmailVerified: Boolean): Future[Try[Int]] = {
    db.run(accountQuery.filter(_.accountId === accountId).map(_.isEmailVerified).update(isEmailVerified).asTry)
      .map {
        case Success(affectedCount) => if (affectedCount > 0) Success(affectedCount) else Failure(new SQLException("Nothing to update"))
        case Failure(ex) => Failure(ex)
      }
  }

  def get(accountId: AccountId): Future[Option[Account]] = {
    db.run(accountQuery.filter(_.accountId === accountId).result.headOption)
  }

  def getByUsername(username: String): Future[Option[Account]] = {
    db.run(accountQuery.filter(_.username === username).result.headOption)
  }

  def getByEmail(email: String): Future[Option[Account]] = {
    db.run(accountQuery.filter(_.email === email).result.headOption)
  }

}


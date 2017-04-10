package services

import dao.AccountsDAO
import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.db.DBApi
import play.api.db.evolutions.Evolutions
import play.api.libs.json.{JsObject, Json}
import play.api.test.Helpers._
import requests.account._
import requests.account.AccountRequests._
/**
  * Created by richardroque on Apr/01/2017.
  */
class AccountServiceIntegrationTest extends PlaySpec with BeforeAndAfterAll with OneAppPerSuite {

  val accountDAO = app.injector.instanceOf(classOf[AccountsDAO])
  val jwtService = app.injector.instanceOf(classOf[JwtService])
  val accountService = app.injector.instanceOf(classOf[AccountService])

  override def beforeAll = {
    val db = app.injector.instanceOf[DBApi].database("default")
    Evolutions.applyEvolutions(db)
  }

  override def afterAll = {
    val db = app.injector.instanceOf[DBApi].database("default")
    Evolutions.cleanupEvolutions(db)
  }

  "AccountService " should {
    "successfully register a new account" in {
      val req = RegisterAccountRequest("Richard Roque", "someUsername", "somePassword", "roquerichardneil@gmail.com")
      await(accountDAO.getByUsername("someUsername")) mustBe None
      await(accountService.register(req)).toOption must not be None
      await(accountDAO.getByUsername("someUsername")) must not be None
    }

    "successfully verify an account" in {
      await(accountDAO.getByUsername("someUsername")) match {
        case Some(account) =>
          val req = VerifyAccountEmailRequest(verifyEmail = true, account.accountId)
          val token = await(jwtService.generateJwt(Json.toJson(req).as[JsObject], 30))
          account.isEmailVerified mustBe false
          await(accountService.verifyAccountEmail(token)).toOption mustBe Some(1)
          val newAccount = await(accountDAO.getByUsername("someUsername")).get
          newAccount.isEmailVerified mustBe true
        case _ =>
      }
    }
  }

}

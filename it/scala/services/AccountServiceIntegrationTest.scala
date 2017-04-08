package services

import dao.AccountsDAO
import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.db.DBApi
import play.api.db.evolutions.Evolutions
import play.api.libs.json.{JsObject, Json}
import play.api.test.Helpers._
import requests.accounts.{RegisterAccountRequest, VerifyAccountEmailRequest}

/**
  * Created by richardroque on Apr/01/2017.
  */
class AccountServiceIntegrationTest extends PlaySpec with BeforeAndAfterAll with OneAppPerSuite {

  val accountDAO = app.injector.instanceOf(classOf[AccountsDAO])
  val service = app.injector.instanceOf(classOf[AccountService])
  val jwtService = app.injector.instanceOf(classOf[JwtService])

  override def beforeAll = {
    val db = app.injector.instanceOf[DBApi].database("default")
    Evolutions.applyEvolutions(db)
  }

  override def afterAll = {
    val db = app.injector.instanceOf[DBApi].database("default")
    Evolutions.cleanupEvolutions(db)
  }

  "AccountService " should {
    "successfully register" in {
      val req = RegisterAccountRequest("Richard Roque", "someUsername", "somePassword", "roquerichardneil@gmail.com")
      await(accountDAO.findByUsername("someUsername")) mustBe None
      await(service.register(req)).toOption must not be None
      await(accountDAO.findByUsername("someUsername")) must not be None
    }

    "successfully verifyAccount" in {
      await(accountDAO.findByUsername("someUsername")) match {
        case Some(account) => {
          val req = VerifyAccountEmailRequest(verifyEmail = true, account.accountId, account.email)
          val token = await(jwtService.generateJwt(Json.toJson(req).as[JsObject], 30))
          account.isEmailVerified mustBe false
          await(service.verifyAccountEmail(token)).toOption mustBe Some(1)
          val newAccount = await(accountDAO.findByUsername("someUsername")).get
          newAccount.isEmailVerified mustBe true
        }
        case _ =>
      }
    }
  }

}

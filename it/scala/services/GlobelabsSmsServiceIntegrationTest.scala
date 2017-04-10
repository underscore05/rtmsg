package services

import dao.{AccountsDAO, ShortcodesDAO}
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll}
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.Logger
import play.api.test.Helpers._
import play.api.db.DBApi
import play.api.db.evolutions.Evolutions
import tables.{Account, Shortcode, ShortcodeTypes}

/**
  * Created by richardroque on Apr/01/2017.
  */
class GlobelabsSmsServiceIntegrationTest extends PlaySpec with BeforeAndAfterAll with OneAppPerSuite {

  val accountsDAO = app.injector.instanceOf(classOf[AccountsDAO])
  val shortcodesDAO = app.injector.instanceOf(classOf[ShortcodesDAO])
  val globelabsService = app.injector.instanceOf(classOf[GlobelabsSmsService])

  override def beforeAll = {
    val db = app.injector.instanceOf[DBApi].database("default")
    Evolutions.applyEvolutions(db)
  }

  override def afterAll = {
    val db = app.injector.instanceOf[DBApi].database("default")
    Evolutions.cleanupEvolutions(db)
  }

  "GlobelabsSmsService Optin" should {

    val accountId = await(accountsDAO.insert(Account(0L, "Account#1", "accountOne", "accountPasswordOne", "accountOne@mailinator.com"))).get
    val shortcodeId = await(shortcodesDAO.insert(Shortcode("2158", accountId, ShortcodeTypes.GLOBELABS, "someAppId", "someAppSecret"))).get

    "save in both subscribers and globelabs_subscribers" when {
      "details is complete" in {
        await(globelabsService.optIn(shortcodeId, "9158242653", "some-user-token")) must not be None
      }
    }
    "fail" when {
      "already exist" in {
        await(globelabsService.optIn(shortcodeId, "9158242653", "some-user-token")).toOption mustBe None
      }
    }
  }


}

package services

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll}
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.Logger
import play.api.test.Helpers._
import play.api.db.DBApi
import play.api.db.evolutions.Evolutions

/**
  * Created by richardroque on Apr/01/2017.
  */
class GlobelabsSmsServiceIntegrationTest extends PlaySpec with BeforeAndAfterAll with OneAppPerSuite {

  val service = app.injector.instanceOf(classOf[GlobelabsSmsService])

  override def beforeAll = {
    val db = app.injector.instanceOf[DBApi].database("default")
    Evolutions.applyEvolutions(db)
  }

  override def afterAll = {
    val db = app.injector.instanceOf[DBApi].database("default")
    Evolutions.cleanupEvolutions(db)
  }

  "GlobelabsSmsService Optin" should {
    "save in both subscribers and globelabs_subscribers" when {
      "details is complete" in {
        await(service.optIn("9158242653", "1421", "some-user-token")) must not be None
      }
    }
    "fail" when {
      "already exist" in {
        await(service.optIn("9158242653", "1421", "some-user-token")) mustBe None
      }
    }
  }


}

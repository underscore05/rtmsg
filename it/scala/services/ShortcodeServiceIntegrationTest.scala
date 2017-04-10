package services

import dao.AccountsDAO
import filters.UserIdentity
import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.db.DBApi
import play.api.db.evolutions.Evolutions
import play.api.test.Helpers._
import requests.shortcode.CreateShortcodeRequest
import tables._

/**
  * Created by richardroque on Apr/01/2017.
  */
class ShortcodeServiceIntegrationTest extends PlaySpec with BeforeAndAfterAll with OneAppPerSuite {

  val accountDAO = app.injector.instanceOf(classOf[AccountsDAO])
  val shortcodeService = app.injector.instanceOf(classOf[ShortcodeService])

  override def beforeAll = {
    val db = app.injector.instanceOf[DBApi].database("default")
    Evolutions.applyEvolutions(db)
  }

  override def afterAll = {
    val db = app.injector.instanceOf[DBApi].database("default")
    Evolutions.cleanupEvolutions(db)
  }

  "ShortcodeService " should {
    "successfully create new shortcode" in {
      val tempAccount = Account(0L, "Test Account", "Test Username", "TestPassword", "testemail@mailinator.com", isEmailVerified = true)
      val accountId = await(accountDAO.insert(tempAccount)).get
      val account = tempAccount.copy(accountId = accountId)

      val userIdentity = UserIdentity(accountId, account.username)
      val request = CreateShortcodeRequest("21584502", ShortcodeTypes.GLOBELABS, "KgE5FMzBpLuX8c7adXTBpKu5qgBqF4jb", "b18516d7c08b309562d944112fb77cb52c77b20c3245b17710eb139dd32ef32a")
      await(shortcodeService.create(userIdentity, request)) must not be None
    }

  }

}

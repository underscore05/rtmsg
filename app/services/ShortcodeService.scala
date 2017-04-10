package services

import javax.inject.Inject

import dao.ShortcodesDAO
import filters.UserIdentity
import requests.shortcode.CreateShortcodeRequest
import tables.{Shortcode, ShortcodeId}

import scala.concurrent.Future
import scala.util.Try

/**
  * Created by richardroque on Apr/09/2017.
  */
class ShortcodeService @Inject()(shortcodesDAO: ShortcodesDAO) {

  def create(userIdentity: UserIdentity, req: CreateShortcodeRequest): Future[Try[ShortcodeId]] = {
    val data = Shortcode(req.shortcodeId, userIdentity.accountId, req.shortcodeType, req.shortcodeAppId, req.shortcodeAppSecret)
    shortcodesDAO.insert(data)
  }

}

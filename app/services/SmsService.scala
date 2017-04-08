package services

import javax.inject.Inject

import dao.SubscribersDAO
import tables.Subscriber

/**
  * Created by richardroque on Apr/01/2017.
  */
class SmsService @Inject()(subscriberDAO: SubscribersDAO) {

  def optIn(msisdn: String, shortcode: String) = {
    subscriberDAO.insert(Subscriber(0L, msisdn, shortcode))
  }
}

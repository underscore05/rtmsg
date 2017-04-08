package controllers

import com.google.inject.Singleton
import play.api.mvc.{Action, Controller}

/**
  * Created by richardroque on Apr/01/2017.
  */
@Singleton
class SmsController extends Controller {

  def sendMessage = Action {
    Ok
  }
}

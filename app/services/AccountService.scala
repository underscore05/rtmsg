package services

import javax.inject.Inject

import dao.AccountsDAO
import org.mindrot.jbcrypt.BCrypt
import play.api.libs.json.{JsObject, Json}
import play.api.libs.mailer.{Email, MailerClient}
import requests.accounts._
import tables.{Account, AccountId}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

/**
  * Created by richardroque on Mar/31/2017.
  */
class AccountService @Inject()(accountsDAO: AccountsDAO, jwtService: JwtService, mailerClient: MailerClient)
                              (implicit ec: ExecutionContext) {

  def register(req: RegisterAccountRequest): Future[Try[AccountId]] = {
    for {
      encryptedPassword <- Future {
        BCrypt.hashpw(req.password, BCrypt.gensalt())
      }
      accountId <- accountsDAO.insert(Account(0L, req.name, req.username, encryptedPassword, req.email))
      _ <- {
        accountId match {
          case Success(aId) =>
            val payload = VerifyAccountEmailRequest(verifyEmail = true, aId, req.email)
            for {
              verificationToken <- jwtService.generateJwt(Json.toJson(payload).as[JsObject], 300)
              _ <- Future {
                val body = s"You verification token is $verificationToken"
                val email = Email("Verify Email", "dev.rroque@gmail.com", Seq(req.email), Some(body))
                mailerClient.send(email)
              }
            } yield verificationToken
          case _ => Future.successful("")
        }
      }
    } yield accountId
  }

  def resendVerificationEmail(req: ResendEmailVerificationRequest): Future[Try[Boolean]] = {
    // TODO: Add check to avoid spamming of email
    for {
      maybeAccount <- accountsDAO.findByEmail(req.email)
      result <- {
        maybeAccount match {
          case Some(account) if !account.isEmailVerified => {
            val payload = VerifyAccountEmailRequest(verifyEmail = true, account.accountId, req.email)
            for {
              verificationToken <- jwtService.generateJwt(Json.toJson(payload).as[JsObject], 300)
              _ <- Future {
                val body = s"You verification token is $verificationToken"
                val email = Email("Verify Email", "dev.rroque@gmail.com", Seq(req.email), Some(body))
                mailerClient.send(email)
              }
            } yield Success(true)
          }
          case Some(account) => Future.successful(Failure(new Exception("Already verified.")))
          case _ => Future.successful(Success(true))
        }
      }
    } yield result
  }

  def login(req: LoginAccountRequest): Future[Try[String]] = {
    // TODO: Check if user email is already verified
    for {
      someAccount <- accountsDAO.findByUsername(req.username)
      someAccountToken <- Future {
        someAccount match {
          case Some(account) if BCrypt.checkpw(req.password, account.password) =>  {
            // TODO: Generate token
            Success("somestring")
          }
          case _ => Failure(new Exception("Incorrect username and password"))
        }
      }
    } yield someAccountToken
  }

  def getUser(id: String): Option[(String, String)] = {
    AccountService.getUser(id)
  }

  def verifyAccountEmail(token: String): Future[Try[Int]] = {
    for {
      validationResult <- jwtService.validateJwt(token)
      updateResult <- {
        validationResult match {
          case Success(jsObj) =>
            jsObj.validate[VerifyAccountEmailRequest].map { req =>
              accountsDAO.updateEmailVerification(req.accountId, req.email, isEmailVerified = true)
            } recoverTotal { parsingError =>
              Future.successful(Failure(new Exception))
            }
          case Failure(ex) => Future.successful(Failure(ex))
        }
      }
    } yield updateResult
  }

}


object AccountService {
  private val users = Map(
    "0001" -> ("Richard Neil Roque", "21584502")
  )

  private def getUser(id: String): Option[(String, String)] = users.get(id)

}



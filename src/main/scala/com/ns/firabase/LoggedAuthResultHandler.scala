package com.ns.firabase

import com.firebase.client.{FirebaseError, AuthData}
import com.firebase.client.Firebase.AuthResultHandler

class LoggedAuthResultHandler extends AuthResultHandler {
  override def onAuthenticated(authData: AuthData): Unit = {
    println(s"successfully Authenticated $authData")
  }

  override def onAuthenticationError(firebaseError: FirebaseError): Unit = {
    println(s"error while authentication: $firebaseError")
  }
}

object LoggedAuthResultHandler {
  def apply() = new LoggedAuthResultHandler
}

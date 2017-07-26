package com.fsanaulla.model
import spray.json.{DeserializationException, JsArray, JsBoolean, JsString}

case class UserInfo(username: String, isAdmin: Boolean)

object UserInfo {
  implicit object UserInfoInfluxReader extends InfluxReader[UserInfo] {
    override def read(js: JsArray): UserInfo = js.elements match {
      case Vector(JsString(username), JsBoolean(admin)) => UserInfo(username, admin)
      case _ => throw DeserializationException(s"Can't deserialize $UserInfo object")
    }
  }
}

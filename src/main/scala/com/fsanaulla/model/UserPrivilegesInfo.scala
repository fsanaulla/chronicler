package com.fsanaulla.model

import spray.json.{DeserializationException, JsArray, JsBoolean, JsString}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 25.07.17
  */
case class UserPrivilegesInfo(database: String, privilege: String)

object UserPrivilegesInfo {
  implicit object UserInfoInfluxReader extends InfluxReader[UserPrivilegesInfo] {
    override def read(js: JsArray): UserPrivilegesInfo = js.elements match {
      case Vector(JsString(username), JsString(admin)) => UserPrivilegesInfo(username, admin)
      case _ => throw DeserializationException(s"Can't deserialize $UserPrivilegesInfo object")
    }
  }
}

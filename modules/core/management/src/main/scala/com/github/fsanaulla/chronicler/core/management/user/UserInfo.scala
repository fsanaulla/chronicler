package com.github.fsanaulla.chronicler.core.management.user

import org.typelevel.jawn.ast.{JArray, JValue}
import com.github.fsanaulla.chronicler.core.model.InfluxReader
import com.github.fsanaulla.chronicler.core.jawn._
import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.model.ParsingException

final case class UserInfo(username: String, isAdmin: Boolean)

object UserInfo {
  implicit val reader: InfluxReader[UserInfo] = new InfluxReader[UserInfo] {
    override def read(js: JArray): ErrorOr[UserInfo] = js.vs match {
      case Array(username: JValue, admin: JValue) =>
        Right(UserInfo(username, admin))
      case _ =>
        Left(new ParsingException(s"Can't deserialize $UserInfo object"))
    }

    override def readUnsafe(js: JArray): UserInfo = js.vs match {
      case Array(username: JValue, admin: JValue) =>
        UserInfo(username, admin)
      case _ =>
        throw new ParsingException(s"Can't deserialize $UserInfo object")
    }
  }
}

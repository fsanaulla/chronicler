package com.github.fsanaulla.chronicler.core.management.user

import org.typelevel.jawn.ast.{JArray, JValue}
import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.model.InfluxReader
import com.github.fsanaulla.chronicler.core.jawn._
import com.github.fsanaulla.chronicler.core.enums.{Privilege, Privileges}
import com.github.fsanaulla.chronicler.core.model.ParsingException

final case class UserPrivilegesInfo(database: String, privilege: Privilege)

object UserPrivilegesInfo {
  implicit object UserPrivilegesInfoInfluxReader extends InfluxReader[UserPrivilegesInfo] {
    override def read(js: JArray): ErrorOr[UserPrivilegesInfo] = js.vs match {
      case Array(username: JValue, privilege: JValue) =>
        Privileges
          .withNameOption(privilege)
          .fold[ErrorOr[UserPrivilegesInfo]](
            Left(new IllegalArgumentException(s"Unsupported privilege: $privilege"))
          )(p => Right(UserPrivilegesInfo(username, p)))
      case _ =>
        Left(new ParsingException(s"Can't deserialize $UserPrivilegesInfo object"))
    }

    override def readUnsafe(js: JArray): UserPrivilegesInfo = js.vs match {
      case Array(username: JValue, privilege: JValue) =>
        UserPrivilegesInfo(username, Privileges.withName(privilege))
      case _ =>
        throw new ParsingException(s"Can't deserialize $UserPrivilegesInfo object")
    }
  }
}

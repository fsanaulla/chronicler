package com.github.fsanaulla.chronicler.core.management.db

import org.typelevel.jawn.ast.{JArray, JValue}
import com.github.fsanaulla.chronicler.core.model.InfluxReader
import com.github.fsanaulla.chronicler.core.jawn._
import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.model.ParsingException

final case class FieldInfo(fieldName: String, fieldType: String)

object FieldInfo {
  implicit val reader = new InfluxReader[FieldInfo] {
    override def read(js: JArray): ErrorOr[FieldInfo] = js.vs match {
      case Array(fieldName: JValue, fieldType: JValue) =>
        Right(FieldInfo(fieldName, fieldType))
      case _ =>
        Left(new ParsingException(s"Can't deserialize FieldInfo object"))
    }

    override def readUnsafe(js: JArray): FieldInfo = js.vs match {
      case Array(fieldName: JValue, fieldType: JValue) =>
        FieldInfo(fieldName, fieldType)
      case _ =>
        throw new ParsingException(s"Can't deserialize FieldInfo object")
    }
  }
}

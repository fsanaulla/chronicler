package com.github.fsanaulla.chronicler.core.management.db

import org.typelevel.jawn.ast.{JArray, JValue}
import com.github.fsanaulla.chronicler.core.model.InfluxReader
import com.github.fsanaulla.chronicler.core.jawn._
import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.model.ParsingException

final case class TagValue(tag: String, value: String)

object TagValue {
  implicit val reader = new InfluxReader[TagValue] {
    override def read(js: JArray): ErrorOr[TagValue] = js.vs match {
      case Array(tag: JValue, value: JValue) =>
        Right(TagValue(tag, value))
      case _ =>
        Left(new ParsingException(s"Can't deserialize TagValue object"))
    }

    override def readUnsafe(js: JArray): TagValue = js.vs match {
      case Array(tag: JValue, value: JValue) =>
        TagValue(tag, value)
      case _ =>
        throw new ParsingException(s"Can't deserialize TagValue object")
    }
  }
}

package com.github.fsanaulla.chronicler.core.management.cq

import org.typelevel.jawn.ast.{JArray, JValue}
import com.github.fsanaulla.chronicler.core.model.InfluxReader
import com.github.fsanaulla.chronicler.core.jawn._
import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.model.ParsingException

final case class ContinuousQuery(cqName: String, query: String)

object ContinuousQuery {
  implicit object ContinuousQueryInfluxReader extends InfluxReader[ContinuousQuery] {
    override def read(js: JArray): ErrorOr[ContinuousQuery] = js.vs match {
      case Array(cqName: JValue, query: JValue) =>
        Right(ContinuousQuery(cqName, query))
      case _ =>
        Left(new ParsingException(s"Can't deserialize $ContinuousQuery object"))
    }

    override def readUnsafe(js: JArray): ContinuousQuery = js.vs match {
      case Array(cqName: JValue, query: JValue) =>
        ContinuousQuery(cqName, query)
      case _ =>
        throw new ParsingException(s"Can't deserialize $ContinuousQuery object")
    }
  }
}

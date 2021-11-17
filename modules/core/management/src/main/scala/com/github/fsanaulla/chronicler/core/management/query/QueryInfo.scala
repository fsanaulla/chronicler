package com.github.fsanaulla.chronicler.core.management.query

import org.typelevel.jawn.ast.{JArray, JValue}
import com.github.fsanaulla.chronicler.core.model.InfluxReader
import com.github.fsanaulla.chronicler.core.jawn._
import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.model.ParsingException

final case class QueryInfo(queryId: Int, query: String, dbName: String, duration: String)

object QueryInfo {
  implicit val reader: InfluxReader[QueryInfo] = new InfluxReader[QueryInfo] {
    override def read(js: JArray): ErrorOr[QueryInfo] = js.vs match {
      case Array(queryId: JValue, query: JValue, dbName: JValue, duration: JValue) =>
        Right(QueryInfo(queryId, query, dbName, duration))
      case _ =>
        Left(new ParsingException(s"Can't deserialize $QueryInfo object"))
    }

    override def readUnsafe(js: JArray): QueryInfo = js.vs match {
      case Array(queryId: JValue, query: JValue, dbName: JValue, duration: JValue) =>
        QueryInfo(queryId, query, dbName, duration)
      case _ =>
        throw new ParsingException(s"Can't deserialize $QueryInfo object")
    }
  }
}

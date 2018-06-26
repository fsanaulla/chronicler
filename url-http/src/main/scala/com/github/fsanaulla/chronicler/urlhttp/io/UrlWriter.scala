package com.github.fsanaulla.chronicler.urlhttp.io

import com.github.fsanaulla.chronicler.core.enums.{Consistency, Precision}
import com.github.fsanaulla.chronicler.core.io.WriteOperations
import com.github.fsanaulla.chronicler.core.model.{HasCredentials, WriteResult}
import com.github.fsanaulla.chronicler.core.query.DatabaseOperationQuery
import com.github.fsanaulla.chronicler.core.utils.PointTransformer
import com.github.fsanaulla.chronicler.urlhttp.handlers.{UrlQueryHandler, UrlRequestHandler, UrlResponseHandler}
import com.softwaremill.sttp.Uri

import scala.io.Source
import scala.util.Try

private[fsanaulla] trait UrlWriter
  extends DatabaseOperationQuery[Uri]
    with UrlRequestHandler
    with UrlResponseHandler
    with UrlQueryHandler
    with PointTransformer
    with HasCredentials { self: WriteOperations[Try, String] =>

  override def writeTo(dbName: String,
                       entity: String,
                       consistency: Consistency,
                       precision: Precision,
                       retentionPolicy: Option[String]): Try[WriteResult] = {
    writeRequest(
      uri = writeToInfluxQuery(
        dbName,
        consistency,
        precision,
        retentionPolicy
      ),
      entity = entity
    ).flatMap(toResult)
  }

  override def writeFromFile(dbName: String,
                             filePath: String,
                             consistency: Consistency,
                             precision: Precision,
                             retentionPolicy: Option[String]): Try[WriteResult] = {
    writeRequest(
      uri = writeToInfluxQuery(
        dbName,
        consistency,
        precision,
        retentionPolicy
      ),
      entity = Source.fromFile(filePath).getLines().mkString("\n")
    ).flatMap(toResult)
  }

}

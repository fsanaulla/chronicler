package com.github.fsanaulla.chronicler.async.io

import com.github.fsanaulla.chronicler.async.handlers._
import com.github.fsanaulla.chronicler.core.enums.{Consistency, Precision}
import com.github.fsanaulla.chronicler.core.io.WriteOperations
import com.github.fsanaulla.chronicler.core.model.{HasCredentials, WriteResult}
import com.github.fsanaulla.chronicler.core.query.DatabaseOperationQuery
import com.github.fsanaulla.chronicler.core.utils.PointTransformer
import com.softwaremill.sttp.Uri

import scala.concurrent.Future
import scala.io.Source

private[fsanaulla] trait AsyncWriter
  extends DatabaseOperationQuery[Uri]
    with AsyncRequestHandler
    with AsyncResponseHandler
    with AsyncQueryHandler
    with PointTransformer
    with HasCredentials { self: WriteOperations[Future, String] =>

  override def writeTo(dbName: String,
                      entity: String,
                      consistency: Consistency,
                      precision: Precision,
                      retentionPolicy: Option[String]): Future[WriteResult] = {
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
                             retentionPolicy: Option[String]): Future[WriteResult] = {
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

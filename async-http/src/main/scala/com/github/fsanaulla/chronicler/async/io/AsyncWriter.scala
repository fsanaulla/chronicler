package com.github.fsanaulla.chronicler.async.io

import com.github.fsanaulla.chronicler.async.handlers._
import com.github.fsanaulla.chronicler.async.utils.ResponseFormats.asJson
import com.github.fsanaulla.chronicler.core.enums.{Consistency, Precision}
import com.github.fsanaulla.chronicler.core.io.WriteOperations
import com.github.fsanaulla.chronicler.core.model.{HasCredentials, WriteResult}
import com.github.fsanaulla.chronicler.core.query.DatabaseOperationQuery
import com.github.fsanaulla.chronicler.core.utils.PointTransformer
import com.softwaremill.sttp.{Empty, RequestT, Uri, sttp}

import scala.concurrent.Future
import scala.io.Source

private[fsanaulla] trait AsyncWriter
  extends DatabaseOperationQuery[Uri]
    with AsyncRequestHandler
    with AsyncResponseHandler
    with AsyncQueryHandler
    with PointTransformer
    with HasCredentials
    with WriteOperations[Future, String] {

  private final val gzipEncoding = "gzip"

  private val reqBuilder: RequestT[Empty, String, Nothing] = sttp

  override def writeTo(dbName: String,
                       entity: String,
                       consistency: Consistency,
                       precision: Precision,
                       retentionPolicy: Option[String],
                       gzipped: Boolean): Future[WriteResult] = {

    val uri = writeToInfluxQuery(dbName, consistency, precision, retentionPolicy)
    val req = reqBuilder
      .post(uri)
      .body(entity)
      .response(asJson)
    val maybeEncoded = if (gzipped) req.acceptEncoding(gzipEncoding) else req

    execute(maybeEncoded).flatMap(toResult)
  }

  override def writeFromFile(dbName: String,
                             filePath: String,
                             consistency: Consistency,
                             precision: Precision,
                             retentionPolicy: Option[String],
                             gzipped: Boolean): Future[WriteResult] = {

    val uri = writeToInfluxQuery(dbName, consistency, precision, retentionPolicy)
    val req = reqBuilder
      .post(uri)
      .body(Source.fromFile(filePath).getLines().mkString("\n"))
      .response(asJson)
    val maybeEncoded = if (gzipped) req.acceptEncoding(gzipEncoding) else req

    execute(maybeEncoded).flatMap(toResult)
  }
}

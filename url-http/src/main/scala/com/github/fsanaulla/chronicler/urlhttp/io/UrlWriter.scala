package com.github.fsanaulla.chronicler.urlhttp.io

import com.github.fsanaulla.chronicler.core.enums.{Consistency, Precision}
import com.github.fsanaulla.chronicler.core.io.WriteOperations
import com.github.fsanaulla.chronicler.core.model.{HasCredentials, WriteResult}
import com.github.fsanaulla.chronicler.core.query.DatabaseOperationQuery
import com.github.fsanaulla.chronicler.core.utils.{Encodings, PointTransformer}
import com.github.fsanaulla.chronicler.urlhttp.handlers.{UrlQueryHandler, UrlRequestHandler, UrlResponseHandler}
import com.github.fsanaulla.chronicler.urlhttp.utils.ResponseFormats.asJson
import com.softwaremill.sttp.{Uri, sttp}

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
                       retentionPolicy: Option[String],
                       gzipped: Boolean): Try[WriteResult] = {
    val uri = writeToInfluxQuery(dbName, consistency, precision, retentionPolicy)
    val req = sttp
      .post(uri)
      .body(entity)
      .response(asJson)
    val maybeEncoded = if (gzipped) req.acceptEncoding(Encodings.gzipEncoding) else req

    execute(maybeEncoded).flatMap(toResult)
  }

  override def writeFromFile(dbName: String,
                             filePath: String,
                             consistency: Consistency,
                             precision: Precision,
                             retentionPolicy: Option[String],
                             gzipped: Boolean): Try[WriteResult] = {
    val uri = writeToInfluxQuery(dbName, consistency, precision, retentionPolicy)
    val req = sttp
      .post(uri)
      .body(Source.fromFile(filePath).getLines().mkString("\n"))
      .response(asJson)
    val maybeEncoded = if (gzipped) req.acceptEncoding(Encodings.gzipEncoding) else req

    execute(maybeEncoded).flatMap(toResult)
  }

}

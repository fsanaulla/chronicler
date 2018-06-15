package com.github.fsanaulla.chronicler.urlhttp.io

import com.github.fsanaulla.chronicler.urlhttp.handlers.{UrlQueryHandler, UrlRequestHandler, UrlResponseHandler}
import com.softwaremill.sttp.Uri

import scala.util.Try

private[fsanaulla] trait UrlWriter
  extends DatabaseOperationQuery[Uri]
    with UrlRequestHandler
    with UrlResponseHandler
    with UrlQueryHandler
    with PointTransformer
    with HasCredentials { self: WriteOperations[Try, String] =>

  override def writeTo(
                        dbName: String,
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

}

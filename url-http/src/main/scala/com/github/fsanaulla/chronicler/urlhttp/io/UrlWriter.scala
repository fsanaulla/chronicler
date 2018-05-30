package com.github.fsanaulla.chronicler.urlhttp.io

import com.github.fsanaulla.chronicler.urlhttp.handlers.{UrlQueryHandler, UrlRequestHandler, UrlResponseHandler}
import com.github.fsanaulla.core.enums.{Consistency, Precision}
import com.github.fsanaulla.core.io.WriteOperations
import com.github.fsanaulla.core.model.{HasCredentials, Result}
import com.github.fsanaulla.core.query.DatabaseOperationQuery
import com.github.fsanaulla.core.utils.PointTransformer
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
                        retentionPolicy: Option[String]): Try[Result] = {
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

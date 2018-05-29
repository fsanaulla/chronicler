package com.github.fsanaulla.chronicler.async.io

import com.github.fsanaulla.chronicler.async.handlers._
import com.github.fsanaulla.core.enums.{Consistency, Precision}
import com.github.fsanaulla.core.io.WriteOperations
import com.github.fsanaulla.core.model.{HasCredentials, InfluxCredentials, Result}
import com.github.fsanaulla.core.query.DatabaseOperationQuery
import com.github.fsanaulla.core.utils.PointTransformer
import com.softwaremill.sttp.Uri

import scala.concurrent.Future

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
                      retentionPolicy: Option[String]): Future[Result] = {
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

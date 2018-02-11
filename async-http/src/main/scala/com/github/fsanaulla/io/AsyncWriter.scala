package com.github.fsanaulla.io

import com.github.fsanaulla.core.io.WriteOperations
import com.github.fsanaulla.core.model.{InfluxCredentials, Result}
import com.github.fsanaulla.core.query.DatabaseOperationQuery
import com.github.fsanaulla.core.utils.PointTransformer
import com.github.fsanaulla.core.utils.constants.Consistencys.Consistency
import com.github.fsanaulla.core.utils.constants.Precisions.Precision
import com.github.fsanaulla.handlers._
import com.softwaremill.sttp.Uri

import scala.concurrent.Future

private[fsanaulla] trait AsyncWriter
  extends DatabaseOperationQuery[Uri]
    with AsyncRequestHandler
    with AsyncResponseHandler
    with AsyncQueryHandler
    with PointTransformer { self: WriteOperations[String] =>

  protected implicit val credentials: InfluxCredentials

  override def _write(dbName: String,
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

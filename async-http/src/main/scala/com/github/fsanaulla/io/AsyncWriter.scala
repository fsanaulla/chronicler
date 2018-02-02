package com.github.fsanaulla.io

import com.github.fsanaulla.handlers._
import com.github.fsanaulla.model.Result
import com.github.fsanaulla.query.DatabaseOperationQuery
import com.github.fsanaulla.utils.PointTransformer
import com.github.fsanaulla.utils.constants.Consistencys.Consistency
import com.github.fsanaulla.utils.constants.Precisions.Precision
import com.softwaremill.sttp.Uri

import scala.concurrent.Future

private[fsanaulla] trait AsyncWriter
  extends DatabaseOperationQuery[Uri]
    with AsyncRequestHandler
    with AsyncResponseHandler
    with AsyncQueryHandler
    with PointTransformer { self: WriteOperations[String] =>

  override def write0(dbName: String,
                      entity: String,
                      consistency: Consistency,
                      precision: Precision,
                      retentionPolicy: Option[String]): Future[Result]

}

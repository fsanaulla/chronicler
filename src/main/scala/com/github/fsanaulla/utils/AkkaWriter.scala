package com.github.fsanaulla.utils

import akka.http.scaladsl.model.RequestEntity
import akka.stream.ActorMaterializer
import com.github.fsanaulla.api.WriteOperations
import com.github.fsanaulla.model.{InfluxCredentials, Result}
import com.github.fsanaulla.query.DatabaseOperationQuery
import com.github.fsanaulla.utils.TypeAlias.Connection
import com.github.fsanaulla.utils.constants.Consistencys.Consistency
import com.github.fsanaulla.utils.constants.Precisions.Precision

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 03.09.17
  */
private[fsanaulla] trait AkkaWriter
  extends DatabaseOperationQuery
    with AkkaRequestHandler
    with AkkaResponseHandler
    with WriteOperations[RequestEntity]
    with PointTransformer {

  implicit val credentials: InfluxCredentials
  implicit val ex: ExecutionContext
  implicit val mat: ActorMaterializer
  implicit val connection: Connection

  protected def write(dbName: String,
                      entity: RequestEntity,
                      consistency: Consistency,
                      precision: Precision,
                      retentionPolicy: Option[String]): Future[Result] = {

    buildRequest(
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

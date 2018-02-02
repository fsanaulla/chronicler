package com.github.fsanaulla.io

import akka.http.scaladsl.model.{RequestEntity, Uri}
import akka.stream.ActorMaterializer
import com.github.fsanaulla.handlers.{AkkaQueryHandler, AkkaRequestHandler, AkkaResponseHandler}
import com.github.fsanaulla.model.{InfluxCredentials, Result}
import com.github.fsanaulla.query.DatabaseOperationQuery
import com.github.fsanaulla.utils.AkkaTypeAlias.Connection
import com.github.fsanaulla.utils.PointTransformer
import com.github.fsanaulla.utils.constants.Consistencys.Consistency
import com.github.fsanaulla.utils.constants.Precisions.Precision

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 03.09.17
  */
private[fsanaulla] trait AkkaWriter
  extends DatabaseOperationQuery[Uri]
    with AkkaRequestHandler
    with AkkaResponseHandler
    with AkkaQueryHandler
    with PointTransformer { self: WriteOperations[RequestEntity] =>

  protected implicit val credentials: InfluxCredentials
  protected implicit val ex: ExecutionContext
  protected implicit val mat: ActorMaterializer
  protected implicit val connection: Connection

  protected def write0(dbName: String,
                       entity: RequestEntity,
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

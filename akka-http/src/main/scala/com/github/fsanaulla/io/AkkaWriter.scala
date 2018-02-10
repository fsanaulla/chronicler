package com.github.fsanaulla.io

import akka.http.scaladsl.model.{RequestEntity, Uri}
import akka.stream.ActorMaterializer
import com.github.fsanaulla.core.io.WriteOperations
import com.github.fsanaulla.core.model.{InfluxCredentials, Result}
import com.github.fsanaulla.core.query.DatabaseOperationQuery
import com.github.fsanaulla.core.utils.PointTransformer
import com.github.fsanaulla.core.utils.constants.Consistencys.Consistency
import com.github.fsanaulla.core.utils.constants.Precisions.Precision
import com.github.fsanaulla.handlers.{AkkaQueryHandler, AkkaRequestHandler, AkkaResponseHandler}
import com.github.fsanaulla.utils.AkkaTypeAlias.Connection

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

  def _write(dbName: String,
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

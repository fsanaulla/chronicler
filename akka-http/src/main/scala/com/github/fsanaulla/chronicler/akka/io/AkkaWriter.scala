package com.github.fsanaulla.chronicler.akka.io

import akka.http.scaladsl.model.{HttpMethods, RequestEntity, Uri}
import akka.stream.ActorMaterializer
import com.github.fsanaulla.chronicler.akka.handlers.{AkkaQueryHandler, AkkaRequestHandler, AkkaResponseHandler}
import com.github.fsanaulla.chronicler.akka.utils.AkkaTypeAlias.Connection
import com.github.fsanaulla.core.enums.{Consistency, Precision}
import com.github.fsanaulla.core.io.WriteOperations
import com.github.fsanaulla.core.model.{Executable, HasCredentials, Result}
import com.github.fsanaulla.core.query.DatabaseOperationQuery
import com.github.fsanaulla.core.utils.PointTransformer

import scala.concurrent.Future

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
    with PointTransformer
    with HasCredentials
    with Executable { self: WriteOperations[Future, RequestEntity] =>

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

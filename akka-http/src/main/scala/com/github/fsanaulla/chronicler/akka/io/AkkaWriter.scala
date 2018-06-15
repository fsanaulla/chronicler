package com.github.fsanaulla.chronicler.akka.io

import akka.http.scaladsl.model.{RequestEntity, Uri}
import akka.stream.ActorMaterializer
import com.github.fsanaulla.chronicler.akka.handlers.{AkkaQueryHandler, AkkaRequestHandler, AkkaResponseHandler}
import com.github.fsanaulla.chronicler.akka.utils.AkkaAlias.Connection
import com.github.fsanaulla.chronicler.core.enums.{Consistency, Precision}
import com.github.fsanaulla.chronicler.core.io.WriteOperations
import com.github.fsanaulla.chronicler.core.model.{Executable, HasCredentials, WriteResult}
import com.github.fsanaulla.chronicler.core.query.DatabaseOperationQuery
import com.github.fsanaulla.chronicler.core.utils.PointTransformer

import scala.concurrent.Future

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 03.09.17
  */
private[akka] trait AkkaWriter
  extends DatabaseOperationQuery[Uri]
    with AkkaRequestHandler
    with AkkaResponseHandler
    with AkkaQueryHandler
    with PointTransformer
    with HasCredentials
    with Executable { self: WriteOperations[Future, RequestEntity] =>

  protected implicit val mat: ActorMaterializer
  protected implicit val connection: Connection

  def writeTo(dbName: String,
             entity: RequestEntity,
             consistency: Consistency,
             precision: Precision,
             retentionPolicy: Option[String]): Future[WriteResult] = {

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

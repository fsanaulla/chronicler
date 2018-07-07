package com.github.fsanaulla.chronicler.akka.io

import java.nio.file.Paths

import _root_.akka.http.scaladsl.model._
import _root_.akka.stream.ActorMaterializer
import _root_.akka.stream.scaladsl.FileIO
import com.github.fsanaulla.chronicler.akka.handlers.{AkkaQueryHandler, AkkaRequestHandler, AkkaResponseHandler}
import com.github.fsanaulla.chronicler.akka.utils.AkkaAlias.Connection
import com.github.fsanaulla.chronicler.akka.utils.AkkaHeaders
import com.github.fsanaulla.chronicler.core.enums.{Consistency, Precision}
import com.github.fsanaulla.chronicler.core.io.WriteOperations
import com.github.fsanaulla.chronicler.core.model.{Executable, HasCredentials, PointTransformer, WriteResult}
import com.github.fsanaulla.chronicler.core.query.DatabaseOperationQuery

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

  override def writeTo(dbName: String,
                       entity: RequestEntity,
                       consistency: Consistency,
                       precision: Precision,
                       retentionPolicy: Option[String],
                       gzipped: Boolean): Future[WriteResult] = {

    val request = HttpRequest(
      uri = writeToInfluxQuery(
        dbName,
        consistency,
        precision,
        retentionPolicy
      ),
      method = HttpMethods.POST,
      headers = if (gzipped) AkkaHeaders.gzipEncoding :: Nil else Nil,
      entity = entity
    )

    execute(request).flatMap(toResult)
  }

  override def writeFromFile(dbName: String,
                             filePath: String,
                             consistency: Consistency,
                             precision: Precision,
                             retentionPolicy: Option[String],
                             gzipped: Boolean): Future[WriteResult] = {

    val request = HttpRequest(
      uri = writeToInfluxQuery(
        dbName,
        consistency,
        precision,
        retentionPolicy
      ),
      method = HttpMethods.POST,
      headers = if (gzipped) AkkaHeaders.gzipEncoding :: Nil else Nil,
      entity = HttpEntity(MediaTypes.`application/octet-stream`, FileIO.fromPath(Paths.get(filePath), 1024))
    )

    execute(request).flatMap(toResult)
  }
}

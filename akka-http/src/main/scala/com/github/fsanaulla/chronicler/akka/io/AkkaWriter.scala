package com.github.fsanaulla.chronicler.akka.io

import java.nio.file.Paths

import _root_.akka.http.scaladsl.model.{ContentTypes, HttpEntity, RequestEntity, Uri}
import _root_.akka.stream.scaladsl.{FileIO, Framing, Source}
import _root_.akka.stream.{ActorMaterializer, IOResult}
import _root_.akka.util.ByteString
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

  override def writeTo(dbName: String,
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

  override def writeFromFile(dbName: String,
                             filePath: String,
                             consistency: Consistency,
                             precision: Precision,
                             retentionPolicy: Option[String]): Future[WriteResult] = {

    val fileSrc: Source[ByteString, Future[IOResult]] = FileIO
      .fromPath(Paths.get(filePath))
      .via(Framing.delimiter(ByteString(System.lineSeparator()), 1024, allowTruncation = true))

    writeRequest(
      uri = writeToInfluxQuery(
        dbName,
        consistency,
        precision,
        retentionPolicy
      ),
      entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, fileSrc)
    ).flatMap(toResult)
  }
}

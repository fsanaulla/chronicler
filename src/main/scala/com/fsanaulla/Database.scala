package com.fsanaulla

import akka.http.scaladsl.model.HttpMethods.POST
import akka.http.scaladsl.model.{HttpEntity, HttpRequest, HttpResponse, MediaTypes}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import com.fsanaulla.entity.Writable
import com.fsanaulla.query.DatabaseQuerys
import com.fsanaulla.utils.TypeAlias._

import scala.concurrent.Future

/**
  * Created by fayaz on 04.07.17.
  */
class Database(dbName: String, connection: ConnectionPoint)(implicit mat: ActorMaterializer) extends DatabaseQuerys {

  def write[T](dbName: String, entity: T)(implicit writer: Writable[T]): Future[HttpResponse] = {
    Source.single(
      HttpRequest(
        method = POST,
        uri = writeToDB(dbName),
        entity = HttpEntity(MediaTypes.`application/octet-stream`, ByteString(writer.write(entity)))
      )
    )
      .via(connection)
      .runWith(Sink.head)
  }

  def bulkWrite[T](dbName: String, entitys: Seq[T])(implicit writer: Writable[T]): Future[HttpResponse] = {

    val influxEntitys = entitys.map(writer.write).mkString("\n")

    Source.single(
      HttpRequest(
        method = POST,
        uri = writeToDB(dbName),
        entity = HttpEntity(MediaTypes.`application/octet-stream`, ByteString(influxEntitys))
      )
    )
      .via(connection)
      .runWith(Sink.head)
  }
}

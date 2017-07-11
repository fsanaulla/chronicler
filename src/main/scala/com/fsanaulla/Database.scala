package com.fsanaulla

import akka.http.scaladsl.model.HttpMethods.{GET, POST}
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
class Database(dbName: String,
               connection: ConnectionPoint,
               username: Option[String] = None,
               password: Option[String] = None
               )(implicit mat: ActorMaterializer) extends DatabaseQuerys {

  def deleteSeries(measurementName: String): Future[HttpResponse] = {
    Source.single(
      HttpRequest(
        method = POST,
        uri = dropMeasurement(dbName, measurementName)
      )
    )
      .via(connection)
      .runWith(Sink.head)
  }

  def write[T](entity: T)(implicit writer: Writable[T]): Future[HttpResponse] = {
    Source.single(
      HttpRequest(
        method = POST,
        uri = writeToDB(dbName = dbName, username = username, password = password),
        entity = HttpEntity(MediaTypes.`application/octet-stream`, ByteString(writer.write(entity)))
      )
    )
      .via(connection)
      .runWith(Sink.head)
  }

  def bulkWrite[T](entitys: Seq[T])(implicit writer: Writable[T]): Future[HttpResponse] = {

    val influxEntitys = entitys.map(writer.write).mkString("\n")

    Source.single(
      HttpRequest(
        method = POST,
        uri = writeToDB(dbName, username = username, password = password),
        entity = HttpEntity(MediaTypes.`application/octet-stream`, ByteString(influxEntitys))
      )
    )
      .via(connection)
      .runWith(Sink.head)
  }

  def read(query: String): Future[HttpResponse] = {
    Source.single(
      HttpRequest(
        method = GET,
        uri = readFromDB(dbName = dbName, query = query, username = username, password = password)
      )
    )
      .via(connection)
      .runWith(Sink.head)
  }
}

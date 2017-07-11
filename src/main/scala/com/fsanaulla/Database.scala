package com.fsanaulla

import akka.http.scaladsl.model.HttpMethods.{GET, POST}
import akka.http.scaladsl.model.{HttpEntity, HttpRequest, HttpResponse, MediaTypes}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import com.fsanaulla.model.Writable
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

  def write[T](measurement: String, entity: T)(implicit writer: Writable[T]): Future[HttpResponse] = {
    Source.single(
      HttpRequest(
        method = POST,
        uri = writeToDB(dbName, username, password),
        entity = HttpEntity(
          MediaTypes.`application/octet-stream`,
          ByteString(toInfluxPoint(measurement, writer.write(entity)))
        )
      )
    )
      .via(connection)
      .runWith(Sink.head)
  }

  def bulkWrite[T](measurement: String, entitys: Seq[T])(implicit writer: Writable[T]): Future[HttpResponse] = {
    Source.single(
      HttpRequest(
        method = POST,
        uri = writeToDB(dbName, username = username, password = password),
        entity = HttpEntity(
          MediaTypes.`application/octet-stream`,
          ByteString(toInfluxPoints(measurement, entitys.map(writer.write))))
      )
    )
      .via(connection)
      .runWith(Sink.head)
  }

  def read(query: String): Future[HttpResponse] = {
    Source.single(
      HttpRequest(
        method = GET,
        uri = readFromDB(dbName, username, password, query)
      )
    )
      .via(connection)
      .runWith(Sink.head)
  }

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

  private def toInfluxPoint(measurement: String, serializedEntity: String) = measurement + "," + serializedEntity
  private def toInfluxPoints(measurement: String, serializedEntitys: Seq[String]) = serializedEntitys.map(s => measurement + "," + s).mkString("\n")
}

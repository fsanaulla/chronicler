package com.fsanaulla

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpMethods.{GET, POST}
import akka.http.scaladsl.model.{HttpEntity, HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import com.fsanaulla.model.{InfluxReader, InfluxWriter}
import com.fsanaulla.query.DatabaseQuerys
import com.fsanaulla.utils.ContentTypes._
import com.fsanaulla.utils.TypeAlias._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by fayaz on 04.07.17.
  */
class Database(dbName: String,
               connection: ConnectionPoint,
               username: Option[String] = None,
               password: Option[String] = None
               )(override implicit val actorSystem: ActorSystem,
                 override implicit val mat: ActorMaterializer,
                 implicit val ex: ExecutionContext) extends DatabaseQuerys with DatabaseHelper {

  def write[T](measurement: String, entity: T)(implicit writer: InfluxWriter[T]): Future[HttpResponse] = {
    Source.single(
      HttpRequest(
        method = POST,
        uri = writeToDB(dbName, username, password),
        entity = HttpEntity(
          octetStream,
          ByteString(toInfluxPoint(measurement, writer.write(entity)))
        )
      )
    )
      .via(connection)
      .runWith(Sink.head)
  }

  def bulkWrite[T](measurement: String, entitys: Seq[T])(implicit writer: InfluxWriter[T]): Future[HttpResponse] = {
    Source.single(
      HttpRequest(
        method = POST,
        uri = writeToDB(dbName, username = username, password = password),
        entity = HttpEntity(
          octetStream,
          ByteString(toInfluxPoints(measurement, entitys.map(writer.write))))
      )
    )
      .via(connection)
      .runWith(Sink.head)
  }

  def read[T](query: String)(implicit reader: InfluxReader[T]): Future[Seq[T]] = {
    Source.single(
      HttpRequest(
        method = GET,
        uri = readFromDB(dbName, username, password, query)
      )
    )
      .via(connection)
      .runWith(Sink.head)
      .flatMap(toJson)
      .map(_.map(reader.read))
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
}

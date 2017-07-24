package com.fsanaulla

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpMethods.{GET, POST}
import akka.http.scaladsl.model.{HttpEntity, HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import com.fsanaulla.model.TypeAlias._
import com.fsanaulla.model.{InfluxReader, InfluxWriter}
import com.fsanaulla.query.DatabaseQuery
import com.fsanaulla.utils.ContentTypes._
import com.fsanaulla.utils.DatabaseHelper
import spray.json.JsArray

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by fayaz on 04.07.17.
  */
class Database(dbName: String, username: Option[String] = None, password: Option[String] = None)
              (implicit val actorSystem: ActorSystem,
               val mat: ActorMaterializer,
               val ex: ExecutionContext,
               val connection: ConnectionPoint)
  extends DatabaseQuery with DatabaseHelper {

  def write[T](measurement: String, entity: T)(implicit writer: InfluxWriter[T]): Future[HttpResponse] = {
    Source.single(
      HttpRequest(
        method = POST,
        uri = writeToInfluxQuery(dbName, username, password),
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
        uri = writeToInfluxQuery(dbName, username = username, password = password),
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
        uri = readFromInfluxSingleQuery(dbName, query, username, password)
      )
    )
      .via(connection)
      .runWith(Sink.head)
      .flatMap(singleQueryResult)
      .map(_.map(reader.read))
  }

  def readPure(query: String): Future[Seq[InfluxPoint]] = {
    Source.single(
      HttpRequest(
        method = GET,
        uri = readFromInfluxSingleQuery(dbName, query, username, password)
      )
    )
      .via(connection)
      .runWith(Sink.head)
      .flatMap(singleQueryResult)
  }

  def bulkRead(querys: Seq[String]): Future[Seq[Seq[InfluxPoint]]] = {
    Source.single(
      HttpRequest(
        method = GET,
        uri = readFromInfluxBulkQuery(dbName, querys, username, password)
      )
    )
      .via(connection)
      .runWith(Sink.head)
      .flatMap(bulkQueryResult)
  }

  def deleteSeries(measurementName: String): Future[HttpResponse] = {
    Source.single(
      HttpRequest(
        method = POST,
        uri = dropMeasurementQuery(dbName, measurementName)
      )
    )
      .via(connection)
      .runWith(Sink.head)
  }
}

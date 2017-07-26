package com.fsanaulla.api

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import akka.stream.ActorMaterializer
import akka.util.ByteString
import com.fsanaulla.model.TypeAlias.{ConnectionPoint, InfluxPoint}
import com.fsanaulla.model.{InfluxReader, InfluxWriter}
import com.fsanaulla.query.DatabaseOperationQuery
import com.fsanaulla.utils.ContentTypes.octetStream
import com.fsanaulla.utils.DatabaseHelper._

import scala.concurrent.{ExecutionContext, Future}

abstract class DatabaseOperation(dbName: String, username: Option[String], password: Option[String])
    extends DatabaseOperationQuery
    with RequestBuilder {

  implicit val actorSystem: ActorSystem
  implicit val mat: ActorMaterializer
  implicit val ex: ExecutionContext
  implicit val connection: ConnectionPoint

  def write[T](measurement: String, entity: T)(implicit writer: InfluxWriter[T]): Future[HttpResponse] = {
    buildRequest(
      uri = writeToInfluxQuery(dbName, username, password),
      entity = HttpEntity(octetStream, ByteString(toPoint(measurement, writer.write(entity))))
    )
  }

  def bulkWrite[T](measurement: String, entitys: Seq[T])(implicit writer: InfluxWriter[T]): Future[HttpResponse] = {
    buildRequest(
      uri = writeToInfluxQuery(dbName, username, password),
      entity = HttpEntity(octetStream, ByteString(toPoints(measurement, entitys.map(writer.write))))
    )
  }

  def read[T](query: String)(implicit reader: InfluxReader[T]): Future[Seq[T]] = {
    buildRequest(readFromInfluxSingleQuery(dbName, query, username, password), GET)
      .flatMap(toSingleResult)
      .map(_.map(reader.read))
  }

  def readPure(query: String): Future[Seq[InfluxPoint]] = {
    buildRequest(readFromInfluxSingleQuery(dbName, query, username, password), GET)
      .flatMap(toSingleResult)
  }

  def bulkRead(querys: Seq[String]): Future[Seq[Seq[InfluxPoint]]] = {
    buildRequest(readFromInfluxBulkQuery(dbName, querys, username, password), GET)
      .flatMap(toBulkResult)
  }
}

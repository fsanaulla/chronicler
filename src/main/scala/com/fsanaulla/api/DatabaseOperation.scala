package com.fsanaulla.api

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.HttpMethods.GET
import akka.stream.ActorMaterializer
import akka.util.ByteString
import com.fsanaulla.Database
import com.fsanaulla.model.JsArrayReadeable.JsArrayInfluxReader
import com.fsanaulla.model.TypeAlias.ConnectionPoint
import com.fsanaulla.model._
import com.fsanaulla.query.DatabaseOperationQuery
import com.fsanaulla.utils.ContentTypes.octetStream
import com.fsanaulla.utils.{DatabaseOperationHelper, ResponseWrapper}
import spray.json.JsArray

import scala.concurrent.{ExecutionContext, Future}

abstract class DatabaseOperation(dbName: String,
                                 username: Option[String],
                                 password: Option[String])
  extends DatabaseOperationQuery with DatabaseOperationHelper with RequestBuilder with ResponseWrapper{ self: Database =>

  implicit val actorSystem: ActorSystem
  implicit val mat: ActorMaterializer
  implicit val ex: ExecutionContext
  implicit val connection: ConnectionPoint

  def write[T](measurement: String, entity: T)(implicit writer: InfluxWriter[T]): Future[WriteResult] = {
    buildRequest(
      uri = writeToInfluxQuery(dbName, username, password),
      entity = HttpEntity(octetStream, ByteString(toPoint(measurement, writer.write(entity))))
    ).flatMap(resp => toResponse(resp, WriteResult(204, isSuccess = true)))
  }

  def bulkWrite[T](measurement: String, entitys: Seq[T])(implicit writer: InfluxWriter[T]): Future[WriteResult] = {
    buildRequest(
      uri = writeToInfluxQuery(dbName, username, password),
      entity = HttpEntity(octetStream, ByteString(toPoints(measurement, entitys.map(writer.write))))
    ).flatMap(resp => toResponse(resp, WriteResult(204, isSuccess = true)))
  }

  def read[T](query: String)(implicit reader: InfluxReader[T]): Future[Seq[T]] = readJs(query).map(_.map(reader.read))

  def readJs(query: String): Future[Seq[JsArray]] = {
    buildRequest(readFromInfluxSingleQuery(dbName, query, username, password), GET)
      .flatMap(response => toQueryResponse[JsArray](response, toSingleResult(response)))
  }

  def bulkReadJs(querys: Seq[String]): Future[Seq[Seq[JsArray]]] = {
    buildRequest(readFromInfluxBulkQuery(dbName, querys, username, password), GET)
      .flatMap(response => toQueryResponse[Seq[JsArray]](response, toBulkResult(response)))
//      Future.sequence(querys.map(readJs))
  }
}

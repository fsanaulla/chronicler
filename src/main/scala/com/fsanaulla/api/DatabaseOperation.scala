package com.fsanaulla.api

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.HttpMethods.GET
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.FileIO
import akka.util.ByteString
import com.fsanaulla.Database
import com.fsanaulla.model._
import com.fsanaulla.query.DatabaseOperationQuery
import com.fsanaulla.utils.ContentTypes.octetStream
import com.fsanaulla.utils.ResponseWrapper.{toBulkQueryJsResult, toQueryJsResult, toQueryResult, toResult}
import com.fsanaulla.utils.TypeAlias.ConnectionPoint
import spray.json.JsArray

import scala.concurrent.{ExecutionContext, Future}

private[fsanaulla] abstract class DatabaseOperation(dbName: String,
                                 username: Option[String],
                                 password: Option[String])
  extends DatabaseOperationQuery with RequestBuilder { self: Database =>
  import DatabaseOperation._

  implicit val actorSystem: ActorSystem
  implicit val mat: ActorMaterializer
  implicit val ex: ExecutionContext
  implicit val connection: ConnectionPoint

  def write[T](measurement: String, entity: T)(implicit writer: InfluxWriter[T]): Future[Result] = {
    buildRequest(
      uri = writeToInfluxQuery(dbName, username, password),
      entity = HttpEntity(octetStream, ByteString(toPoint(measurement, writer.write(entity))))
    ).flatMap(toResult)
  }

  def bulkWrite[T](measurement: String, entitys: Seq[T])(implicit writer: InfluxWriter[T]): Future[Result] = {
    buildRequest(
      uri = writeToInfluxQuery(dbName, username, password),
      entity = HttpEntity(octetStream, ByteString(toPoints(measurement, entitys.map(writer.write))))
    ).flatMap(toResult)
  }

  def writeFromFile(path: String, chunkSize: Int = 8192): Future[Result] = {
    buildRequest(
      uri = writeToInfluxQuery(dbName, username, password),
      entity = HttpEntity(octetStream, FileIO.fromPath(Paths.get(path), chunkSize = chunkSize))
    ).flatMap(toResult)
  }

  def read[T](query: String)(implicit reader: InfluxReader[T]): Future[QueryResult[T]] = {
    buildRequest(readFromInfluxSingleQuery(dbName, query, username, password), GET).flatMap(toQueryResult[T])
  }

  def readJs(query: String): Future[QueryResult[JsArray]] = {
    buildRequest(readFromInfluxSingleQuery(dbName, query, username, password), GET).flatMap(toQueryJsResult)
  }

  def bulkReadJs(querys: Seq[String]): Future[QueryResult[Seq[JsArray]]] = {
    buildRequest(readFromInfluxBulkQuery(dbName, querys, username, password), GET).flatMap(toBulkQueryJsResult)
  }
}

object DatabaseOperation {

  def toPoint(measurement: String, serializedEntity: String): String = measurement + "," + serializedEntity

  def toPoints(measurement: String, serializedEntitys: Seq[String]): String = serializedEntitys.map(s => measurement + "," + s).mkString("\n")
}

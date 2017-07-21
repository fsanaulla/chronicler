package com.fsanaulla.api

import akka.http.scaladsl.model.HttpMethods.POST
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.scaladsl.{Sink, Source}
import com.fsanaulla.query.DatabaseManagementQuery
import com.fsanaulla.{Database, InfluxClient}

import scala.concurrent.Future

trait DatabaseManagement extends DatabaseManagementQuery { self: InfluxClient =>

  def createDatabase(dbName: String): Future[HttpResponse] = {
    Source.single(
      HttpRequest(
        method = POST,
        uri = createDBQuery(dbName))
    )
      .via(connection)
      .runWith(Sink.head)
  }

  def dropDatabase(dbName: String): Future[HttpResponse] = {
    Source.single(
      HttpRequest(
        method = POST,
        uri = dropDBQuery(dbName))
    )
      .via(connection)
      .runWith(Sink.head)
  }

  def use(dbName: String): Database = new Database(dbName)
}

package com.fsanaulla.api

import akka.http.scaladsl.model.HttpResponse
import com.fsanaulla.query.DatabaseManagementQuery
import com.fsanaulla.{Database, InfluxClient}

import scala.concurrent.Future

trait DatabaseManagement extends DatabaseManagementQuery with RequestBuilder { self: InfluxClient =>

  def createDatabase(dbName: String): Future[HttpResponse] = {
    buildRequest(createDatabaseQuery(dbName))
  }

  def dropDatabase(dbName: String): Future[HttpResponse] = {
    buildRequest(dropDatabaseQuery(dbName))
  }

  def use(dbName: String): Database = new Database(dbName)
}

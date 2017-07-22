package com.fsanaulla.api

import akka.http.scaladsl.model.HttpMethods.POST
import akka.http.scaladsl.model.{HttpMethod, HttpRequest, HttpResponse, Uri}
import akka.stream.scaladsl.{Sink, Source}
import com.fsanaulla.InfluxClient
import com.fsanaulla.model.TypeAlias.ConnectionPoint
import com.fsanaulla.query.UserManagementQuery

import scala.concurrent.Future

trait UserManagement extends UserManagementQuery { self: InfluxClient =>

  def createUser(username: String, password: String): Future[HttpResponse] = {
     userManagementRequest(createUserQuery(username, password))
   }

  def dropUser(username: String): Future[HttpResponse] = userManagementRequest(uri = dropUserQuery(username))

  def setUserPassword(username: String, password: String): Future[HttpResponse] = ???

  def setPrivileges(username: String, database: String, privilege: String): Future[HttpResponse] = ???

  def removePrivileges(username: String, database: String, privilege: String): Future[HttpResponse] = ???

  def makeAdmin(username: String): Future[HttpResponse] = ???

  private def userManagementRequest(uri: Uri, method: HttpMethod = POST)(implicit connection: ConnectionPoint) = {
    Source.single(
      HttpRequest(
        method = method,
        uri = uri
      )
    )
      .via(connection)
      .runWith(Sink.head)
  }
}

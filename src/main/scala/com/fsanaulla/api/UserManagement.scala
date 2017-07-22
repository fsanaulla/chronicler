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
     buildRequest(createUserQuery(username, password))
  }

  def createAdmin(username: String, password: String): Future[HttpResponse] = {
    buildRequest(createAdminQuery(username, password))
  }

  def dropUser(username: String): Future[HttpResponse] = {
    buildRequest(uri = dropUserQuery(username))
  }

  def setUserPassword(username: String, password: String): Future[HttpResponse] = {
    buildRequest(setUserPasswordQuery(username, password))
  }

  def setPrivileges(username: String, dbName: String, privilege: String): Future[HttpResponse] = {
    buildRequest(setPrivilegesQuery(dbName, username, privilege))
  }

  def revovePrivileges(username: String, dbName: String, privilege: String): Future[HttpResponse] = {
    buildRequest(revokePrivilegesQuery(dbName, username, privilege))
  }

  def makeAdmin(username: String): Future[HttpResponse] = {
    buildRequest(makeAdminQuery(username))
  }

  def disableAdmin(username: String): Future[HttpResponse] = {
    buildRequest(disableAdminQuery(username))
  }

  def showUsers: Future[HttpResponse] = {
    buildRequest(showUsersQuery)
  }

  def showUserPrivileges(username: String): Future[HttpResponse] = {
    buildRequest(showUserPrivilegesQuery(username))
  }

  private def buildRequest(uri: Uri, method: HttpMethod = POST)(implicit connection: ConnectionPoint) = {
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

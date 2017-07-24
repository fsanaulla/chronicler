package com.fsanaulla.api

import akka.http.scaladsl.model.HttpResponse
import com.fsanaulla.InfluxClient
import com.fsanaulla.query.UserManagementQuery

import scala.concurrent.Future

trait UserManagement extends UserManagementQuery with RequestBuilder { self: InfluxClient =>

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
}

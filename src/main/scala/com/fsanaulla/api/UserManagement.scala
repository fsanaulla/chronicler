package com.fsanaulla.api

import akka.http.scaladsl.model.HttpResponse
import com.fsanaulla.InfluxClient
import com.fsanaulla.model.{UserInfo, UserPrivilegesInfo}
import com.fsanaulla.query.UserManagementQuery
import com.fsanaulla.utils.UserManagementHelper._
import com.fsanaulla.utils.constants.Privileges._

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

  def setPrivileges(username: String, dbName: String, privilege: Privilege): Future[HttpResponse] = {
    buildRequest(setPrivilegesQuery(dbName, username, privilege))
  }

  def revokePrivileges(username: String, dbName: String, privilege: Privilege): Future[HttpResponse] = {
    buildRequest(revokePrivilegesQuery(dbName, username, privilege))
  }

  def makeAdmin(username: String): Future[HttpResponse] = {
    buildRequest(makeAdminQuery(username))
  }

  def disableAdmin(username: String): Future[HttpResponse] = {
    buildRequest(disableAdminQuery(username))
  }

  def showUsers: Future[Seq[UserInfo]] = {
    buildRequest(showUsersQuery).flatMap(toUserInfo)
  }

  def showUserPrivileges(username: String): Future[Seq[UserPrivilegesInfo]] = {
    buildRequest(showUserPrivilegesQuery(username)).flatMap(toUserPrivilegesInfo)
  }
}

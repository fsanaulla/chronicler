package com.fsanaulla.api

import com.fsanaulla.InfluxClient
import com.fsanaulla.model._
import com.fsanaulla.query.UserManagementQuery
import com.fsanaulla.utils.ResponseWrapper.{toQueryResult, toResult}
import com.fsanaulla.utils.constants.Privileges._

import scala.concurrent.Future

private[fsanaulla] trait UserManagement extends UserManagementQuery { self: InfluxClient =>

  def createUser(username: String, password: String): Future[Result] = {
     buildRequest(createUserQuery(username, password)).flatMap(toResult)
  }

  def createAdmin(username: String, password: String): Future[Result] = {
    buildRequest(createAdminQuery(username, password)).flatMap(toResult)
  }

  def dropUser(username: String): Future[Result] = {
    buildRequest(uri = dropUserQuery(username)).flatMap(toResult)
  }

  def setUserPassword(username: String, password: String): Future[Result] = {
    buildRequest(setUserPasswordQuery(username, password)).flatMap(toResult)
  }

  def setPrivileges(username: String, dbName: String, privilege: Privilege): Future[Result] = {
    buildRequest(setPrivilegesQuery(dbName, username, privilege)).flatMap(toResult)
  }

  def revokePrivileges(username: String, dbName: String, privilege: Privilege): Future[Result] = {
    buildRequest(revokePrivilegesQuery(dbName, username, privilege)).flatMap(toResult)
  }

  def makeAdmin(username: String): Future[Result] = {
    buildRequest(makeAdminQuery(username)).flatMap(toResult)
  }

  def disableAdmin(username: String): Future[Result] = {
    buildRequest(disableAdminQuery(username)).flatMap(toResult)
  }

  def showUsers(implicit reader: InfluxReader[UserInfo]): Future[QueryResult[UserInfo]] = {
    buildRequest(showUsersQuery).flatMap(toQueryResult[UserInfo])
  }

  def showUserPrivileges(username: String)(implicit reader: InfluxReader[UserPrivilegesInfo]): Future[QueryResult[UserPrivilegesInfo]] = {
    buildRequest(showUserPrivilegesQuery(username)).flatMap(toQueryResult[UserPrivilegesInfo])
  }
}

package com.fsanaulla.api

import com.fsanaulla.InfluxClient
import com.fsanaulla.model._
import com.fsanaulla.query.UserManagementQuery
import com.fsanaulla.utils.ResponseWrapper.{toQueryJsResult, toResult}
import com.fsanaulla.utils.constants.Privileges._

import scala.concurrent.Future

trait UserManagement extends UserManagementQuery { self: InfluxClient =>

  def createUser(username: String, password: String): Future[Unit] = {
     buildRequest(createUserQuery(username, password)).flatMap(toResult)
  }

  def createAdmin(username: String, password: String): Future[Unit] = {
    buildRequest(createAdminQuery(username, password)).flatMap(toResult)
  }

  def dropUser(username: String): Future[Unit] = {
    buildRequest(uri = dropUserQuery(username)).flatMap(toResult)
  }

  def setUserPassword(username: String, password: String): Future[Unit] = {
    buildRequest(setUserPasswordQuery(username, password)).flatMap(toResult)
  }

  def setPrivileges(username: String, dbName: String, privilege: Privilege): Future[Unit] = {
    buildRequest(setPrivilegesQuery(dbName, username, privilege)).flatMap(toResult)
  }

  def revokePrivileges(username: String, dbName: String, privilege: Privilege): Future[Unit] = {
    buildRequest(revokePrivilegesQuery(dbName, username, privilege)).flatMap(toResult)
  }

  def makeAdmin(username: String): Future[Unit] = {
    buildRequest(makeAdminQuery(username)).flatMap(toResult)
  }

  def disableAdmin(username: String): Future[Unit] = {
    buildRequest(disableAdminQuery(username)).flatMap(toResult)
  }

  def showUsers(implicit reader: InfluxReader[UserInfo]): Future[Seq[UserInfo]] = {
    buildRequest(showUsersQuery).flatMap(toQueryJsResult).map(_.map(reader.read))
  }

  def showUserPrivileges(username: String)(implicit reader: InfluxReader[UserPrivilegesInfo]): Future[Seq[UserPrivilegesInfo]] = {
    buildRequest(showUserPrivilegesQuery(username)).flatMap(toQueryJsResult).map(_.map(reader.read))
  }
}

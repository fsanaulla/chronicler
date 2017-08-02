package com.fsanaulla.api

import com.fsanaulla.InfluxClient
import com.fsanaulla.model._
import com.fsanaulla.query.UserManagementQuery
import com.fsanaulla.utils.constants.Privileges._

import scala.concurrent.Future

trait UserManagement extends UserManagementQuery { self: InfluxClient =>

  def createUser(username: String, password: String): Future[CreateResult] = {
     buildRequest(createUserQuery(username, password))
      .flatMap(resp => toResult(resp, CreateResult(200, isSuccess = true)))
  }

  def createAdmin(username: String, password: String): Future[CreateResult] = {
    buildRequest(createAdminQuery(username, password))
      .flatMap(resp => toResult(resp, CreateResult(200, isSuccess = true)))
  }

  def dropUser(username: String): Future[DeleteResult] = {
    buildRequest(uri = dropUserQuery(username))
      .flatMap(resp => toResult(resp, DeleteResult(200, isSuccess = true)))
  }

  def setUserPassword(username: String, password: String): Future[UpdateResult] = {
    buildRequest(setUserPasswordQuery(username, password))
      .flatMap(resp => toResult(resp, UpdateResult(200, isSuccess = true)))
  }

  def setPrivileges(username: String, dbName: String, privilege: Privilege): Future[UpdateResult] = {
    buildRequest(setPrivilegesQuery(dbName, username, privilege))
      .flatMap(resp => toResult(resp, UpdateResult(200, isSuccess = true)))
  }

  def revokePrivileges(username: String, dbName: String, privilege: Privilege): Future[DeleteResult] = {
    buildRequest(revokePrivilegesQuery(dbName, username, privilege))
      .flatMap(resp => toResult(resp, DeleteResult(200, isSuccess = true)))
  }

  def makeAdmin(username: String): Future[UpdateResult] = {
    buildRequest(makeAdminQuery(username))
      .flatMap(resp => toResult(resp, UpdateResult(200, isSuccess = true)))
  }

  def disableAdmin(username: String): Future[UpdateResult] = {
    buildRequest(disableAdminQuery(username))
      .flatMap(resp => toResult(resp, UpdateResult(200, isSuccess = true)))
  }

  def showUsers(implicit reader: InfluxReader[UserInfo]): Future[Seq[UserInfo]] = {
    buildRequest(showUsersQuery).flatMap(toQueryJsResult).map(_.map(reader.read))
  }

  def showUserPrivileges(username: String)(implicit reader: InfluxReader[UserPrivilegesInfo]): Future[Seq[UserPrivilegesInfo]] = {
    buildRequest(showUserPrivilegesQuery(username)).flatMap(toQueryJsResult).map(_.map(reader.read))
  }
}

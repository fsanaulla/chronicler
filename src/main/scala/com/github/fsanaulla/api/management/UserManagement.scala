package com.github.fsanaulla.api.management

import com.github.fsanaulla.handlers.{QueryHandler, RequestHandler, ResponseHandler}
import com.github.fsanaulla.model.InfluxImplicits._
import com.github.fsanaulla.model._
import com.github.fsanaulla.query.UserManagementQuery
import com.github.fsanaulla.utils.constants.Privileges.Privilege

import scala.concurrent.{ExecutionContext, Future}

private[fsanaulla] trait UserManagement[R, U, M, E] extends UserManagementQuery[U] {
  self: RequestHandler[R, U, M, E] with ResponseHandler[R] with QueryHandler[U] with HasCredentials =>

  implicit val ex: ExecutionContext

  def createUser(username: String, password: String): Future[Result] = {
    buildRequest(createUserQuery(username, password)).flatMap(toResult)
  }

  def createAdmin(username: String, password: String): Future[Result] = {
    buildRequest(createAdminQuery(username, password)).flatMap(toResult)
  }

  def dropUser(username: String): Future[Result] = {
    buildRequest(dropUserQuery(username)).flatMap(toResult)
  }

  def setUserPassword(username: String, password: String): Future[Result] = {
    buildRequest(setUserPasswordQuery(username, password)).flatMap(toResult)
  }

  def setPrivileges(username: String,
                    dbName: String,
                    privilege: Privilege): Future[Result] = {

    buildRequest(setPrivilegesQuery(dbName, username, privilege)).flatMap(toResult)
  }

  def revokePrivileges(username: String,
                       dbName: String,
                       privilege: Privilege): Future[Result] = {

    buildRequest(revokePrivilegesQuery(dbName, username, privilege)).flatMap(toResult)
  }

  def makeAdmin(username: String): Future[Result] = {
    buildRequest(makeAdminQuery(username)).flatMap(toResult)
  }

  def disableAdmin(username: String): Future[Result] = {
    buildRequest(disableAdminQuery(username)).flatMap(toResult)
  }

  def showUsers(): Future[QueryResult[UserInfo]] = {
    buildRequest(showUsersQuery).flatMap(toQueryResult[UserInfo])
  }

  def showUserPrivileges(username: String): Future[QueryResult[UserPrivilegesInfo]] = {
    buildRequest(showUserPrivilegesQuery(username)).flatMap(toQueryResult[UserPrivilegesInfo])
  }
}

package com.github.fsanaulla.core.api.management

import com.github.fsanaulla.core.enums.Privilege
import com.github.fsanaulla.core.handlers.RequestHandler
import com.github.fsanaulla.core.handlers.query.QueryHandler
import com.github.fsanaulla.core.handlers.response.ResponseHandler
import com.github.fsanaulla.core.model._
import com.github.fsanaulla.core.query.UserManagementQuery
import com.github.fsanaulla.core.utils.DefaultInfluxImplicits._

import scala.concurrent.Future

private[fsanaulla] trait UserManagement[R, U, M, E] extends UserManagementQuery[U] {
  self: RequestHandler[R, U, M, E]
    with ResponseHandler[R]
    with QueryHandler[U]
    with HasCredentials
    with Executable =>

  /***
    * Create new username
    * @param username - Name for new user
    * @param password - Password for new user
    * @return         - Result of execution
    */
  def createUser(username: String, password: String): Future[Result] = {
    readRequest(createUserQuery(username, password)).flatMap(toResult)
  }

  def createAdmin(username: String, password: String): Future[Result] = {
    readRequest(createAdminQuery(username, password)).flatMap(toResult)
  }

  def dropUser(username: String): Future[Result] = {
    readRequest(dropUserQuery(username)).flatMap(toResult)
  }

  def setUserPassword(username: String, password: String): Future[Result] = {
    readRequest(setUserPasswordQuery(username, password)).flatMap(toResult)
  }

  def setPrivileges(username: String,
                    dbName: String,
                    privilege: Privilege): Future[Result] = {

    readRequest(setPrivilegesQuery(dbName, username, privilege)).flatMap(toResult)
  }

  def revokePrivileges(username: String,
                       dbName: String,
                       privilege: Privilege): Future[Result] = {

    readRequest(revokePrivilegesQuery(dbName, username, privilege)).flatMap(toResult)
  }

  def makeAdmin(username: String): Future[Result] = {
    readRequest(makeAdminQuery(username)).flatMap(toResult)
  }

  def disableAdmin(username: String): Future[Result] = {
    readRequest(disableAdminQuery(username)).flatMap(toResult)
  }

  def showUsers(): Future[QueryResult[UserInfo]] = {
    readRequest(showUsersQuery()).flatMap(toQueryResult[UserInfo])
  }

  def showUserPrivileges(username: String): Future[QueryResult[UserPrivilegesInfo]] = {
    readRequest(showUserPrivilegesQuery(username)).flatMap(toQueryResult[UserPrivilegesInfo])
  }
}

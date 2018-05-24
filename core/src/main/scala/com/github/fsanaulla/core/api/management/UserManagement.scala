package com.github.fsanaulla.core.api.management

import com.github.fsanaulla.core.enums.Privilege
import com.github.fsanaulla.core.handlers.{QueryHandler, RequestHandler, ResponseHandler}
import com.github.fsanaulla.core.model._
import com.github.fsanaulla.core.query.UserManagementQuery
import com.github.fsanaulla.core.utils.DefaultInfluxImplicits._

private[fsanaulla] trait UserManagement[M[_], R, U, E] extends UserManagementQuery[U] {
  self: RequestHandler[M, R, U, E]
    with ResponseHandler[M, R]
    with QueryHandler[U]
    with Mappable[M, R]
    with HasCredentials =>

  /***
    * Create new username
    * @param username - Name for new user
    * @param password - Password for new user
    * @return         - Result of execution
    */
  def createUser(username: String, password: String): M[Result] =
    m.mapTo(readRequest(createUserQuery(username, password)), toResult)

  /**
    * Create admin user
    * @param username - admin name
    * @param password - admin password
    * @return         - execution response
    */
  def createAdmin(username: String, password: String): M[Result] =
    m.mapTo(readRequest(createAdminQuery(username, password)), toResult)

  /** Drop user */
  def dropUser(username: String): M[Result] =
    m.mapTo(readRequest(dropUserQuery(username)), toResult)

  /** Set password for user */
  def setUserPassword(username: String, password: String): M[Result] =
    m.mapTo(readRequest(setUserPasswordQuery(username, password)), toResult)

  /** Set user privilege on specified database */
  def setPrivileges(username: String, dbName: String, privilege: Privilege): M[Result] =
    m.mapTo(readRequest(setPrivilegesQuery(dbName, username, privilege)), toResult)


  /** Revoke user privilege on specified datasbase */
  def revokePrivileges(username: String, dbName: String, privilege: Privilege): M[Result] =
    m.mapTo(readRequest(revokePrivilegesQuery(dbName, username, privilege)), toResult)

  /** Grant admin rights */
  def makeAdmin(username: String): M[Result] =
    m.mapTo(readRequest(makeAdminQuery(username)), toResult)

  /** Remove admin rights */
  def disableAdmin(username: String): M[Result] =
    m.mapTo(readRequest(disableAdminQuery(username)), toResult)

  /** Show use lists */
  def showUsers: M[QueryResult[UserInfo]] =
    m.mapTo(readRequest(showUsersQuery()), toQueryResult[UserInfo])

  /** Show user privileges */
  def showUserPrivileges(username: String): M[QueryResult[UserPrivilegesInfo]] =
    m.mapTo(readRequest(showUserPrivilegesQuery(username)), toQueryResult[UserPrivilegesInfo])

}

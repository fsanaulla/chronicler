package com.github.fsanaulla.chronicler.core.api.management

import com.github.fsanaulla.chronicler.core.enums.Privilege
import com.github.fsanaulla.chronicler.core.handlers.{QueryHandler, RequestHandler, ResponseHandler}
import com.github.fsanaulla.chronicler.core.model._
import com.github.fsanaulla.chronicler.core.query.UserManagementQuery

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
  final def createUser(username: String, password: String): M[WriteResult] =
    m.mapTo(readRequest(createUserQuery(username, password)), toResult)

  /**
    * Create admin user
    * @param username - admin name
    * @param password - admin password
    * @return         - execution response
    */
  final def createAdmin(username: String, password: String): M[WriteResult] =
    m.mapTo(readRequest(createAdminQuery(username, password)), toResult)

  /** Drop user */
  final def dropUser(username: String): M[WriteResult] =
    m.mapTo(readRequest(dropUserQuery(username)), toResult)

  /** Set password for user */
  final def setUserPassword(username: String, password: String): M[WriteResult] =
    m.mapTo(readRequest(setUserPasswordQuery(username, password)), toResult)

  /** Set user privilege on specified database */
  final def setPrivileges(username: String, dbName: String, privilege: Privilege): M[WriteResult] =
    m.mapTo(readRequest(setPrivilegesQuery(dbName, username, privilege)), toResult)


  /** Revoke user privilege on specified datasbase */
  final def revokePrivileges(username: String, dbName: String, privilege: Privilege): M[WriteResult] =
    m.mapTo(readRequest(revokePrivilegesQuery(dbName, username, privilege)), toResult)

  /** Grant admin rights */
  final def makeAdmin(username: String): M[WriteResult] =
    m.mapTo(readRequest(makeAdminQuery(username)), toResult)

  /** Remove admin rights */
  final def disableAdmin(username: String): M[WriteResult] =
    m.mapTo(readRequest(disableAdminQuery(username)), toResult)

  /** Show use lists */
  final def showUsers: M[QueryResult[UserInfo]] =
    m.mapTo(readRequest(showUsersQuery()), toQueryResult[UserInfo])

  /** Show user privileges */
  final def showUserPrivileges(username: String): M[QueryResult[UserPrivilegesInfo]] =
    m.mapTo(readRequest(showUserPrivilegesQuery(username)), toQueryResult[UserPrivilegesInfo])

}

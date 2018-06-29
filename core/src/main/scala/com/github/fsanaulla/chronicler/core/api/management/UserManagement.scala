package com.github.fsanaulla.chronicler.core.api.management

import com.github.fsanaulla.chronicler.core.enums.Privilege
import com.github.fsanaulla.chronicler.core.handlers.{QueryHandler, RequestHandler, ResponseHandler}
import com.github.fsanaulla.chronicler.core.model._
import com.github.fsanaulla.chronicler.core.query.UserManagementQuery
import com.github.fsanaulla.chronicler.core.utils.DefaultInfluxImplicits._

private[fsanaulla] trait UserManagement[M[_], Req, Resp, Uri, Entity] extends UserManagementQuery[Uri] {
  self: RequestHandler[M, Req, Resp]
    with ResponseHandler[M, Resp]
    with QueryHandler[Uri]
    with Mappable[M, Resp]
    with RequestBuilder[Uri, Req]
    with HasCredentials =>

  /***
    * Create new username
    * @param username - Name for new user
    * @param password - Password for new user
    * @return         - Result of execution
    */
  final def createUser(username: String, password: String): M[WriteResult] =
    mapTo(execute(createUserQuery(username, password)), toResult)

  /**
    * Create admin user
    * @param username - admin name
    * @param password - admin password
    * @return         - execution response
    */
  final def createAdmin(username: String, password: String): M[WriteResult] =
    mapTo(execute(createAdminQuery(username, password)), toResult)

  /** Drop user */
  final def dropUser(username: String): M[WriteResult] =
    mapTo(execute(dropUserQuery(username)), toResult)

  /** Set password for user */
  final def setUserPassword(username: String, password: String): M[WriteResult] =
    mapTo(execute(setUserPasswordQuery(username, password)), toResult)

  /** Set user privilege on specified database */
  final def setPrivileges(username: String, dbName: String, privilege: Privilege): M[WriteResult] =
    mapTo(execute(setPrivilegesQuery(dbName, username, privilege)), toResult)


  /** Revoke user privilege on specified datasbase */
  final def revokePrivileges(username: String, dbName: String, privilege: Privilege): M[WriteResult] =
    mapTo(execute(revokePrivilegesQuery(dbName, username, privilege)), toResult)

  /** Grant admin rights */
  final def makeAdmin(username: String): M[WriteResult] =
    mapTo(execute(makeAdminQuery(username)), toResult)

  /** Remove admin rights */
  final def disableAdmin(username: String): M[WriteResult] =
    mapTo(execute(disableAdminQuery(username)), toResult)

  /** Show use lists */
  final def showUsers: M[QueryResult[UserInfo]] =
    mapTo(execute(showUsersQuery()), toQueryResult[UserInfo])

  /** Show user privileges */
  final def showUserPrivileges(username: String): M[QueryResult[UserPrivilegesInfo]] =
    mapTo(execute(showUserPrivilegesQuery(username)), toQueryResult[UserPrivilegesInfo])

}

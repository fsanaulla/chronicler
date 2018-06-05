package com.github.fsanaulla.core.query

import com.github.fsanaulla.core.enums.Privilege
import com.github.fsanaulla.core.handlers.QueryHandler
import com.github.fsanaulla.core.model.HasCredentials

private[fsanaulla] trait UserManagementQuery[U] {
  self: QueryHandler[U] with HasCredentials =>

  final def showUsersQuery(): U =
    buildQuery("/query", buildQueryParams("SHOW USERS"))


  final def showUserPrivilegesQuery(username: String): U =
    buildQuery("/query", buildQueryParams(s"SHOW GRANTS FOR $username"))


  final def setUserPasswordQuery(username: String, password: String): U =
    buildQuery("/query", buildQueryParams(s"SET PASSWORD FOR $username = '$password'"))


  final def createAdminQuery(username: String, password: String): U =
    buildQuery("/query", buildQueryParams(s"CREATE USER $username WITH PASSWORD '$password' WITH ALL PRIVILEGES"))


  final def makeAdminQuery(username: String): U =
    buildQuery("/query", buildQueryParams(s"GRANT ALL PRIVILEGES TO $username"))


  final def disableAdminQuery(username: String): U =
    buildQuery("/query", buildQueryParams(s"REVOKE ALL PRIVILEGES FROM $username"))


  final def createUserQuery(username: String, password: String): U =
    buildQuery("/query", buildQueryParams(s"CREATE USER $username WITH PASSWORD '$password'"))


  final def dropUserQuery(username: String): U =
    buildQuery("/query", buildQueryParams(s"DROP USER $username"))


  final def setPrivilegesQuery(dbName: String,
                               username: String,
                               privileges: Privilege): U =
    buildQuery("/query", buildQueryParams(s"GRANT $privileges ON $dbName TO $username"))


  final def revokePrivilegesQuery(dbName: String,
                                  username: String,
                                  privileges: Privilege): U =
    buildQuery("/query", buildQueryParams(s"REVOKE $privileges ON $dbName FROM $username"))

}

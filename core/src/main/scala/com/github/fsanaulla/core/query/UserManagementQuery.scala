package com.github.fsanaulla.core.query

import com.github.fsanaulla.core.enums.Privilege
import com.github.fsanaulla.core.handlers.QueryHandler
import com.github.fsanaulla.core.model.HasCredentials

private[fsanaulla] trait UserManagementQuery[U] {
  self: QueryHandler[U] with HasCredentials =>

  def showUsersQuery(): U = {
    buildQuery("/query", buildQueryParams("SHOW USERS"))
  }

  def showUserPrivilegesQuery(username: String): U = {
    buildQuery("/query", buildQueryParams(s"SHOW GRANTS FOR $username"))
  }

  def setUserPasswordQuery(username: String, password: String): U = {
    buildQuery("/query", buildQueryParams(s"SET PASSWORD FOR $username = '$password'"))
  }

  def createAdminQuery(username: String, password: String): U = {
    buildQuery("/query", buildQueryParams(s"CREATE USER $username WITH PASSWORD '$password' WITH ALL PRIVILEGES"))
  }

  def makeAdminQuery(username: String): U = {
    buildQuery("/query", buildQueryParams(s"GRANT ALL PRIVILEGES TO $username"))
  }

  def disableAdminQuery(username: String): U = {
    buildQuery("/query", buildQueryParams(s"REVOKE ALL PRIVILEGES FROM $username"))
  }

  def createUserQuery(username: String, password: String): U = {
    buildQuery("/query", buildQueryParams(s"CREATE USER $username WITH PASSWORD '$password'"))
  }

  def dropUserQuery(username: String): U = {
    buildQuery("/query", buildQueryParams(s"DROP USER $username"))
  }

  def setPrivilegesQuery(dbName: String,
                         username: String,
                         privileges: Privilege): U = {
    buildQuery("/query", buildQueryParams(s"GRANT $privileges ON $dbName TO $username"))
  }

  def revokePrivilegesQuery(dbName: String,
                            username: String,
                            privileges: Privilege): U = {
    buildQuery("/query", buildQueryParams(s"REVOKE $privileges ON $dbName FROM $username"))
  }
}

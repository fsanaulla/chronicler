package com.fsanaulla.query

import akka.http.scaladsl.model.Uri
import com.fsanaulla.model.InfluxCredentials

private[fsanaulla] trait UserManagementQuery extends QueryBuilder {

  protected def showUsersQuery()(implicit credentials: InfluxCredentials): Uri = {
    buildQuery("/query", buildQueryParams("SHOW USERS"))
  }

  protected def showUserPrivilegesQuery(username: String)(implicit credentials: InfluxCredentials): Uri = {
    buildQuery("/query", buildQueryParams(s"SHOW GRANTS FOR $username"))
  }

  protected def setUserPasswordQuery(username: String, password: String)(implicit credentials: InfluxCredentials): Uri = {
    buildQuery("/query", buildQueryParams(s"SET PASSWORD FOR $username = '$password'"))
  }

  // ADMIN QUERYS
  protected def createAdminQuery(username: String, password: String)(implicit credentials: InfluxCredentials): Uri = {
    buildQuery("/query", buildQueryParams(s"CREATE USER $username WITH PASSWORD '$password' WITH ALL PRIVILEGES"))
  }

  protected def makeAdminQuery(username: String)(implicit credentials: InfluxCredentials): Uri = {
    buildQuery("/query", buildQueryParams(s"GRANT ALL PRIVILEGES TO $username"))
  }

  protected def disableAdminQuery(username: String)(implicit credentials: InfluxCredentials): Uri = {
    buildQuery("/query", buildQueryParams(s"REVOKE ALL PRIVILEGES FROM $username"))
  }

  // USER QUERYS
  protected def createUserQuery(username: String, password: String)(implicit credentials: InfluxCredentials): Uri = {
    buildQuery("/query", buildQueryParams(s"CREATE USER $username WITH PASSWORD '$password'"))
  }

  protected def dropUserQuery(username: String)(implicit credentials: InfluxCredentials): Uri = {
    buildQuery("/query", buildQueryParams(s"DROP USER $username"))
  }

  protected def setPrivilegesQuery(dbName: String, username: String, privileges: String)(implicit credentials: InfluxCredentials): Uri = {
    buildQuery("/query", buildQueryParams(s"GRANT $privileges ON $dbName TO $username"))
  }

  protected def revokePrivilegesQuery(dbName: String, username: String, privileges: String)(implicit credentials: InfluxCredentials): Uri = {
    buildQuery("/query", buildQueryParams(s"REVOKE $privileges ON $dbName FROM $username"))
  }
}

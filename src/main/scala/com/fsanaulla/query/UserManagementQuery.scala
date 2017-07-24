package com.fsanaulla.query

import akka.http.scaladsl.model.Uri

trait UserManagementQuery extends QueryBuilder {

  protected def showUsersQuery: Uri = {
    queryBuilder("SHOW USERS")
  }

  protected def showUserPrivilegesQuery(username: String): Uri = {
    queryBuilder(s"SHOW GRANTS FOR $username")
  }

  protected def setUserPasswordQuery(username: String, password: String): Uri = {
    queryBuilder(s"SET PASSWORD FOR $username = '$password'")
  }

  // ADMIN QUERYS
  protected def createAdminQuery(username: String, password: String): Uri = {
    queryBuilder(s"CREATE USER $username WITH PASSWORD '$password' WITH ALL PRIVILEGES")
  }

  protected def makeAdminQuery(username: String): Uri = {
    queryBuilder(s"GRANT ALL PRIVILEGES TO $username")
  }

  protected def disableAdminQuery(username: String): Uri = {
    queryBuilder(s"REVOKE ALL PRIVILEGES FROM $username")
  }

  // USER QUERYS
  protected def createUserQuery(username: String, password: String): Uri = {
    queryBuilder(s"CREATE USER $username WITH PASSWORD '$password'")
  }

  protected def dropUserQuery(username: String): Uri = {
    queryBuilder(s"DROP USER $username")
  }

  protected def setPrivilegesQuery(dbName: String, username: String, privileges: String): Uri = {
    queryBuilder(s"GRANT $privileges ON $dbName TO $username")
  }

  protected def revokePrivilegesQuery(dbName: String, username: String, privileges: String): Uri = {
    queryBuilder(s"REVOKE $privileges ON $dbName FROM $username")
  }
}

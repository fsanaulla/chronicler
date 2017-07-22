package com.fsanaulla.query

import akka.http.scaladsl.model.Uri

trait UserManagementQuery {

  def showUsersQuery: Uri = {
    queryBuilder("SHOW USERS")
  }

  def showUserPrivilegesQuery(username: String): Uri = {
    queryBuilder(s"SHOW GRANTS FOR $username")
  }

  def setUserPasswordQuery(username: String, password: String): Uri = {
    queryBuilder(s"SET PASSWORD FOR $username = '$password'")
  }

  // ADMIN QUERYS
  def createAdminQuery(username: String, password: String): Uri = {
    queryBuilder(s"CREATE USER $username WITH PASSWORD '$password' WITH ALL PRIVILEGES")
  }

  def makeAdminQuery(username: String): Uri = {
    queryBuilder(s"GRANT ALL PRIVILEGES TO $username")
  }

  def disableAdminQuery(username: String): Uri = {
    queryBuilder(s"REVOKE ALL PRIVILEGES FROM $username")
  }

  // USER QUERYS
  def createUserQuery(username: String, password: String): Uri = {
    queryBuilder(s"CREATE USER $username WITH PASSWORD '$password'")
  }

  def dropUserQuery(username: String): Uri = {
    queryBuilder(s"DROP USER $username")
  }

  def setPrivilegesQuery(dbName: String, username: String, privileges: String): Uri = {
    queryBuilder(s"GRANT $privileges ON $dbName TO $username")
  }

  def revokePrivilegesQuery(dbName: String, username: String, privileges: String): Uri = {
    queryBuilder(s"REVOKE $privileges ON $dbName FROM $username")
  }

  private def queryBuilder(queryParam: String) = {
    Uri("/query").withQuery(Uri.Query("q" -> queryParam))
  }

}

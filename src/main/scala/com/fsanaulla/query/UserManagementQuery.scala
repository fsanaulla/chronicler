package com.fsanaulla.query

import akka.http.scaladsl.model.Uri

trait UserManagementQuery {

  def createUserQuery(username: String, password: String, admin: Boolean): Uri = {
    val userQuery = "CREATE USER $username WITH PASSWORD '$password'"
    val finalQuery = if (admin) userQuery + "WITH ALL PRIVILEGES" else userQuery

    Uri("/query").withQuery(Uri.Query("q" -> finalQuery))
  }

  def dropUserQuery(username: String): Uri = {
    Uri("/query").withQuery(Uri.Query("q" -> s"DROP USER $username"))
  }
}

package com.fsanaulla.unit

import akka.http.scaladsl.model.Uri
import com.fsanaulla.query.UserManagementQuery
import org.scalatest.{FlatSpec, Matchers}

class UserManagementQuerySpec
  extends FlatSpec
  with Matchers
  with UserManagementQuery {

  private val testUsername = "USER"
  private val testPassword = "PASSWORD"
  private val testDatabase = "DATABASE"

  "create user query" should "generate correct query" in {
    createUserQuery(testUsername, testPassword) shouldEqual Uri(s"/query?q=CREATE+USER+$testUsername+WITH+PASSWORD+'$testPassword'")
  }

  "create admin user query" should "generate correct query" in {
    createAdminQuery(testUsername, testPassword) shouldEqual Uri(s"/query?q=CREATE+USER+$testUsername+WITH+PASSWORD+'$testPassword'+WITH+ALL+PRIVILEGES")
  }

//  "drop user query" should "generate correct query" in {
//    dropUserQuery(testUsername) shouldEqual ""
//  }

  //todo: implement other
}

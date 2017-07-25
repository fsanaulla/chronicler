package com.fsanaulla.unit.management.user

import akka.http.scaladsl.model.Uri
import com.fsanaulla.query.UserManagementQuery
import com.fsanaulla.utils.constants.Privileges
import org.scalatest.{FlatSpecLike, Matchers}

class UserManagementQuerySpec
  extends UserManagementQuery
  with FlatSpecLike
  with Matchers {

  private val testUsername = "TEST_USER_NAME"
  private val testPassword = "TEST_PASSWORD"
  private val testDatabase = "TEST_DATABASE"
  private val testPrivilege = Privileges.ALL

  "create user query" should "generate correct query" in {
    createUserQuery(testUsername, testPassword) shouldEqual Uri(s"/query?q=CREATE+USER+$testUsername+WITH+PASSWORD+'$testPassword'")
  }

  "create admin user query" should "generate correct query" in {
    createAdminQuery(testUsername, testPassword) shouldEqual Uri(s"/query?q=CREATE+USER+$testUsername+WITH+PASSWORD+'$testPassword'+WITH+ALL+PRIVILEGES")
  }

  "drop user query" should "generate correct query" in {
    dropUserQuery(testUsername) shouldEqual Uri(s"/query?q=DROP+USER+$testUsername")
  }

  "show users query" should "generate correct query" in {
    showUsersQuery shouldEqual Uri("/query?q=SHOW+USERS")
  }

  "show user privileges query" should "generate correct query" in {
    showUserPrivilegesQuery(testUsername) shouldEqual Uri(s"/query?q=SHOW+GRANTS+FOR+$testUsername")
  }

  "make admin query" should "generate correct query" in {
    makeAdminQuery(testUsername) shouldEqual Uri(s"/query?q=GRANT+ALL+PRIVILEGES+TO+$testUsername")
  }

  "disable admin query" should "generate correct query" in {
    disableAdminQuery(testUsername) shouldEqual Uri(s"/query?q=REVOKE+ALL+PRIVILEGES+FROM+$testUsername")
  }

  "set user password query" should "generate correct query" in {
    setUserPasswordQuery(testUsername, testPassword) shouldEqual Uri(s"/query?q=SET+PASSWORD+FOR+$testUsername+%3D+'$testPassword'")
  }

  "set privileges query" should "generate correct query" in {
    setPrivilegesQuery(testDatabase, testUsername, testPrivilege) shouldEqual Uri(s"/query?q=GRANT+$testPrivilege+ON+$testDatabase+TO+$testUsername")
  }

  "revoke privileges query" should "generate correct query" in {
    revokePrivilegesQuery(testDatabase, testUsername, testPrivilege) shouldEqual Uri(s"/query?q=REVOKE+$testPrivilege+ON+$testDatabase+FROM+$testUsername")
  }

}

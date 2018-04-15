package com.github.fsanaulla.chronicler.akka.integration

import com.github.fsanaulla.chronicler.akka.{InfluxAkkaHttpClient, InfluxDB}
import com.github.fsanaulla.core.enums.Privileges
import com.github.fsanaulla.core.model.{AuthorizationException, UserPrivilegesInfo}
import com.github.fsanaulla.core.test.ResultMatchers._
import com.github.fsanaulla.core.test.{NonEmptyCredentials, TestSpec}
import com.github.fsanaulla.core.testing.configurations.InfluxHTTPConf
import com.github.fsanaulla.scalatest.EmbeddedInfluxDB

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 17.08.17
  */
class AuthenticationSpec
  extends TestSpec
    with NonEmptyCredentials
    with EmbeddedInfluxDB
    with InfluxHTTPConf {

  override def auth: Boolean = true

  val userDB = "db"
  val userName = "some_user"
  val userPass = "some_user_pass"
  val userNPass = "some_new_user_pass"

  val admin = "admin"
  val adminPass = "admin"

  lazy val influx: InfluxAkkaHttpClient = InfluxDB.connect()

  lazy val authInflux: InfluxAkkaHttpClient =
    InfluxDB.connect("localhost", httpPort, credentials)

  "AuthenticationUserManagement" should  "create admin user " in {
    influx.createAdmin(admin, adminPass).futureValue shouldEqual OkResult

    influx.showUsers().futureValue.ex.value shouldBe a[AuthorizationException]

    influx.clone() shouldEqual {}
  }

  it should "create database" in {
    authInflux.createDatabase(userDB).futureValue shouldEqual OkResult
  }
  it should "create user" in {
    authInflux.createUser(userName, userPass).futureValue shouldEqual OkResult
    authInflux.showUsers().futureValue.queryResult.exists(_.username == userName) shouldEqual true
  }

  it should "get admin privileges" in {
    val privs = authInflux.showUserPrivileges(admin).futureValue.queryResult

    privs.length shouldEqual 1
    privs.exists(_.database == userDB) shouldEqual true
  }

  it should "set user password" in {
    authInflux.setUserPassword(userName, userNPass).futureValue shouldEqual OkResult
  }

  it should "set user privileges" in {
    authInflux.setPrivileges(userName, userDB, Privileges.READ).futureValue shouldEqual OkResult
  }

  it should "get user privileges" in {
    val userPrivs = authInflux.showUserPrivileges(userName).futureValue.queryResult

    userPrivs.length shouldEqual 1
    userPrivs.exists { upi =>
      upi.database == userDB && upi.privilege == Privileges.withName("READ")
    } shouldEqual true
  }

  it should "revoke user privileges" in {
    authInflux.revokePrivileges(userName, userDB, Privileges.READ).futureValue shouldEqual OkResult
    authInflux.showUserPrivileges(userName).futureValue.queryResult shouldEqual Array.empty[UserPrivilegesInfo]
  }

  it should "drop user" in {
    authInflux.dropUser(userName).futureValue shouldEqual OkResult
    authInflux.dropUser(admin).futureValue shouldEqual OkResult

    authInflux.close() shouldEqual {}
  }

}

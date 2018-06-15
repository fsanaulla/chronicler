package com.github.fsanaulla.chronicler.async.integration

import com.github.fsanaulla.chronicler.async.{Influx, InfluxAsyncHttpClient}
import com.github.fsanaulla.chronicler.testing.ResultMatchers._
import com.github.fsanaulla.chronicler.testing.{DockerizedInfluxDB, FutureHandler, TestSpec}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 17.08.17
  */
class AuthenticationSpec extends TestSpec with DockerizedInfluxDB with FutureHandler {


  val userDB = "db"
  val userName = "some_user"
  val userPass = "some_user_pass"
  val userNPass = "some_new_user_pass"

  val admin = "admin"
  val adminPass = "admin"

  lazy val influx: InfluxAsyncHttpClient =
    Influx.connect(host, port)

  lazy val authInflux: InfluxAsyncHttpClient =
    Influx.connect(host, port, Some(creds))

  "AuthenticationManagement" should  "create admin user " in {
    influx.showUsers.futureValue.ex.value shouldBe a[AuthorizationException]
  }

  it should "create database" in {
    authInflux.createDatabase(userDB).futureValue shouldEqual OkResult
  }
  it should "create user" in {
    authInflux.createUser(userName, userPass).futureValue shouldEqual OkResult
    authInflux.showUsers.futureValue.result.exists(_.username == userName) shouldEqual true
  }

  it should "set user password" in {
    authInflux.setUserPassword(userName, userNPass).futureValue shouldEqual OkResult
  }

  it should "set user privileges" in {
    authInflux.setPrivileges(userName, userDB, Privileges.READ).futureValue shouldEqual OkResult
  }

  it should "get user privileges" in {
    val userPrivs = authInflux.showUserPrivileges(userName).futureValue.result

    userPrivs.length shouldEqual 1
    userPrivs.exists { upi =>
      upi.database == userDB && upi.privilege == Privileges.withName("READ")
    } shouldEqual true
  }

  it should "revoke user privileges" in {
    authInflux.revokePrivileges(userName, userDB, Privileges.READ).futureValue shouldEqual OkResult
    authInflux.showUserPrivileges(userName).futureValue.result shouldEqual Array(UserPrivilegesInfo(userDB, Privileges.NO_PRIVILEGES))
  }

  it should "drop user" in {
    authInflux.dropUser(userName).futureValue shouldEqual OkResult
    authInflux.dropUser(admin).futureValue shouldEqual OkResult

    authInflux.close() shouldEqual {}
  }
}

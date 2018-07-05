package com.github.fsanaulla.chronicler.async

import com.github.fsanaulla.chronicler.core.enums.Privileges
import com.github.fsanaulla.chronicler.core.model.{AuthorizationException, UserPrivilegesInfo}
import com.github.fsanaulla.chronicler.testing.it.ResultMatchers.OkResult
import com.github.fsanaulla.chronicler.testing.it.{DockerizedInfluxDB, Futures}
import com.github.fsanaulla.chronicler.testing.unit.FlatSpecWithMatchers
import org.scalatest.OptionValues

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 17.08.17
  */
class AuthenticationSpec extends FlatSpecWithMatchers with DockerizedInfluxDB with Futures with OptionValues {

  val userDB = "db"
  val userName = "some_user"
  val userPass = "some_user_pass"
  val userNPass = "some_new_user_pass"

  val admin = "admin"
  val adminPass = "admin"

  lazy val influx: InfluxAsyncHttpClient =
    Influx(host, port)

  lazy val authInflux: InfluxAsyncHttpClient =
    Influx(host, port, Some(creds))

  "AuthenticationManagement" should  "create admin user " in {
    influx.showUsers.futureValue.ex.value shouldBe a[AuthorizationException]
  }

  it should "create database" in {
    authInflux.createDatabase(userDB).futureValue shouldEqual OkResult
  }
  it should "create user" in {
    authInflux.createUser(userName, userPass).futureValue shouldEqual OkResult
    authInflux.showUsers.futureValue.queryResult.exists(_.username == userName) shouldEqual true
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
    authInflux.showUserPrivileges(userName).futureValue.queryResult shouldEqual Array(UserPrivilegesInfo(userDB, Privileges.NO_PRIVILEGES))
  }

  it should "drop user" in {
    authInflux.dropUser(userName).futureValue shouldEqual OkResult
    authInflux.dropUser(admin).futureValue shouldEqual OkResult

    authInflux.close() shouldEqual {}
  }
}

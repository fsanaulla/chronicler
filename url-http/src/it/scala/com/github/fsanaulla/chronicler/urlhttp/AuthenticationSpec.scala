package com.github.fsanaulla.chronicler.urlhttp

import com.github.fsanaulla.chronicler.core.enums.Privileges
import com.github.fsanaulla.chronicler.core.model.{AuthorizationException, UserPrivilegesInfo}
import com.github.fsanaulla.chronicler.testing.it.DockerizedInfluxDB
import com.github.fsanaulla.chronicler.testing.unit.FlatSpecWithMatchers
import org.scalatest.{OptionValues, TryValues}
import com.github.fsanaulla.chronicler.testing.it.ResultMatchers.OkResult

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 17.08.17
  */
class AuthenticationSpec extends FlatSpecWithMatchers with DockerizedInfluxDB with TryValues with OptionValues {


  val userDB = "db"
  val userName = "some_user"
  val userPass = "some_user_pass"
  val userNPass = "some_new_user_pass"

  val admin = "admin"
  val adminPass = "admin"

  lazy val influx: InfluxUrlHttpClient =
    Influx.connect(host, port)

  lazy val authInflux: InfluxUrlHttpClient =
    Influx.connect(host, port, Some(creds))

  "AuthenticationManagement" should  "create admin user " in {
    influx.showUsers.success.value.ex.value shouldBe a[AuthorizationException]
  }

  it should "create database" in {
    authInflux.createDatabase(userDB).success.value shouldEqual OkResult
  }
  it should "create user" in {
    authInflux.createUser(userName, userPass).success.value shouldEqual OkResult
    authInflux.showUsers.success.value.queryResult.exists(_.username == userName) shouldEqual true
  }

  it should "set user password" in {
    authInflux.setUserPassword(userName, userNPass).success.value shouldEqual OkResult
  }

  it should "set user privileges" in {
    authInflux.setPrivileges(userName, userDB, Privileges.READ).success.value shouldEqual OkResult
  }

  it should "get user privileges" in {
    val userPrivs = authInflux.showUserPrivileges(userName).success.value.queryResult

    userPrivs.length shouldEqual 1
    userPrivs.exists { upi =>
      upi.database == userDB && upi.privilege == Privileges.withName("READ")
    } shouldEqual true
  }

  it should "revoke user privileges" in {
    authInflux.revokePrivileges(userName, userDB, Privileges.READ).success.value shouldEqual OkResult
    authInflux.showUserPrivileges(userName).success.value.queryResult shouldEqual Array(UserPrivilegesInfo(userDB, Privileges.NO_PRIVILEGES))
  }

  it should "drop user" in {
    authInflux.dropUser(userName).success.value shouldEqual OkResult
    authInflux.dropUser(admin).success.value shouldEqual OkResult

    authInflux.close() shouldEqual {}
  }
}

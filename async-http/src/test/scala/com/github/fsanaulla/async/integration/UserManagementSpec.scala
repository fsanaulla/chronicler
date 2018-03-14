package com.github.fsanaulla.async.integration

import com.github.fsanaulla.async.utils.TestHelper._
import com.github.fsanaulla.chronicler.async.{InfluxAsyncHttpClient, InfluxDB}
import com.github.fsanaulla.core.model.{UserInfo, UserPrivilegesInfo}
import com.github.fsanaulla.core.test.utils.TestSpec
import com.github.fsanaulla.core.utils.constants.Privileges
import com.github.fsanaulla.scalatest.EmbeddedInfluxDB

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 10.08.17
  */
class UserManagementSpec
  extends TestSpec
    with EmbeddedInfluxDB{

  val userDB = "db"
  val userName = "Martin"
  val userPass = "pass"
  val userNPass = "new_pass"

  val admin = "Admin"
  val adminPass = "admin_pass"

  lazy val influx: InfluxAsyncHttpClient =
    InfluxDB(host = influxHost, port = httpPort)

  "User management operation" should "create user" in {
    influx.createDatabase(userDB).futureValue shouldEqual OkResult

    influx.createUser(userName, userPass).futureValue shouldEqual OkResult
    influx.showUsers().futureValue.queryResult.contains(UserInfo(userName, isAdmin = false)) shouldEqual true
  }

  it should "create admin" in {
    influx.createAdmin(admin, adminPass).futureValue shouldEqual OkResult
    influx.showUsers().futureValue.queryResult.contains(UserInfo(admin, isAdmin = true)) shouldEqual true
  }

  it should "show user privileges" in {
    influx.showUserPrivileges(admin).futureValue.queryResult shouldEqual Nil
  }

  it should "set user password" in {
    influx.setUserPassword(userName, userNPass).futureValue shouldEqual OkResult
  }

  it should "set privileges" in {
    influx.setPrivileges(userName, userDB, Privileges.READ).futureValue shouldEqual OkResult
    influx.setPrivileges("unknown", userDB, Privileges.READ).futureValue.ex.value.getMessage shouldEqual "user not found"

    influx.showUserPrivileges(userName).futureValue.queryResult shouldEqual Seq(UserPrivilegesInfo(userDB, Privileges.READ))
  }

  it should "revoke privileges" in {
    influx.revokePrivileges(userName, userDB, Privileges.READ).futureValue shouldEqual OkResult
    influx.showUserPrivileges(userName).futureValue.queryResult shouldEqual Seq(UserPrivilegesInfo(userDB, Privileges.NO_PRIVILEGES))
  }

  it should "disable admin" in {
    influx.disableAdmin(admin).futureValue shouldEqual OkResult
    influx.showUsers().futureValue.queryResult.contains(UserInfo(admin, isAdmin = false)) shouldEqual true
  }

  it should "make admin" in {
    influx.makeAdmin(admin).futureValue shouldEqual OkResult
    influx.showUsers().futureValue.queryResult.contains(UserInfo(admin, isAdmin = true)) shouldEqual true
  }

  it should "drop users" in {
    influx.dropUser(userName).futureValue shouldEqual OkResult
    influx.dropUser(admin).futureValue shouldEqual OkResult

    influx.close() shouldEqual {}
  }
}

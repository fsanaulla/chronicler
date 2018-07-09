package com.github.fsanaulla.chronicler.async

import com.github.fsanaulla.chronicler.async.clients.AsyncManagementClient
import com.github.fsanaulla.chronicler.core.enums.Privileges
import com.github.fsanaulla.chronicler.core.model.{UserInfo, UserPrivilegesInfo}
import com.github.fsanaulla.chronicler.testing.it.ResultMatchers.OkResult
import com.github.fsanaulla.chronicler.testing.it.{DockerizedInfluxDB, Futures}
import com.github.fsanaulla.chronicler.testing.unit.FlatSpecWithMatchers

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 10.08.17
  */
class UserManagementSpec extends FlatSpecWithMatchers with DockerizedInfluxDB with Futures {

  val userDB = "db"
  val userName = "Martin"
  val userPass = "pass"
  val userNPass = "new_pass"

  val admin = "Admin"
  val adminPass = "admin_pass"

  lazy val influx: AsyncManagementClient =
    Influx.management(host, port, Some(creds))

  "User management operation" should "create user" in {
    influx.createDatabase(userDB).futureValue shouldEqual OkResult

    influx.createUser(userName, userPass).futureValue shouldEqual OkResult
    influx.showUsers.futureValue.queryResult.contains(UserInfo(userName, isAdmin = false)) shouldEqual true
  }

  it should "create admin" in {
    influx.createAdmin(admin, adminPass).futureValue shouldEqual OkResult
    influx.showUsers.futureValue.queryResult.contains(UserInfo(admin, isAdmin = true)) shouldEqual true
  }

  it should "show user privileges" in {
    influx.showUserPrivileges(admin).futureValue.queryResult shouldEqual Nil
  }

  it should "set user password" in {
    influx.setUserPassword(userName, userNPass).futureValue shouldEqual OkResult
  }

  it should "set privileges" in {
    influx.setPrivileges(userName, userDB, Privileges.READ).futureValue shouldEqual OkResult
    influx.setPrivileges("unknown", userDB, Privileges.READ).futureValue.ex.get.getMessage shouldEqual "user not found"

    influx.showUserPrivileges(userName).futureValue.queryResult shouldEqual Array(UserPrivilegesInfo(userDB, Privileges.READ))
  }

  it should "revoke privileges" in {
    influx.revokePrivileges(userName, userDB, Privileges.READ).futureValue shouldEqual OkResult
    influx.showUserPrivileges(userName).futureValue.queryResult shouldEqual Array(UserPrivilegesInfo(userDB, Privileges.NO_PRIVILEGES))
  }

  it should "disable admin" in {
    influx.disableAdmin(admin).futureValue shouldEqual OkResult
    influx.showUsers.futureValue.queryResult.contains(UserInfo(admin, isAdmin = false)) shouldEqual true
  }

  it should "make admin" in {
    influx.makeAdmin(admin).futureValue shouldEqual OkResult
    influx.showUsers.futureValue.queryResult.contains(UserInfo(admin, isAdmin = true)) shouldEqual true
  }

  it should "drop users" in {
    influx.dropUser(userName).futureValue shouldEqual OkResult
    influx.dropUser(admin).futureValue shouldEqual OkResult

    influx.close() shouldEqual {}
  }
}

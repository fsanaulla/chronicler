package com.github.fsanaulla.chronicler.urlhttp

import com.github.fsanaulla.chronicler.core.enums.Privileges
import com.github.fsanaulla.chronicler.core.model.{UserInfo, UserPrivilegesInfo}
import com.github.fsanaulla.chronicler.testing.it.DockerizedInfluxDB
import com.github.fsanaulla.chronicler.testing.it.ResultMatchers.OkResult
import com.github.fsanaulla.chronicler.testing.unit.FlatSpecWithMatchers
import org.scalatest.TryValues

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 10.08.17
  */
class UserManagementSpec extends FlatSpecWithMatchers with DockerizedInfluxDB with TryValues {

  val userDB = "db"
  val userName = "Martin"
  val userPass = "pass"
  val userNPass = "new_pass"

  val admin = "Admin"
  val adminPass = "admin_pass"

  lazy val influx =
    Influx.connect(host, port, Some(creds))

  "User management operation" should "create user" in {
    influx.createDatabase(userDB).success.value shouldEqual OkResult

    influx.createUser(userName, userPass).success.value shouldEqual OkResult
    influx.showUsers.success.value.queryResult.contains(UserInfo(userName, isAdmin = false)) shouldEqual true
  }

  it should "create admin" in {
    influx.createAdmin(admin, adminPass).success.value shouldEqual OkResult
    influx.showUsers.success.value.queryResult.contains(UserInfo(admin, isAdmin = true)) shouldEqual true
  }

  it should "show user privileges" in {
    influx.showUserPrivileges(admin).success.value.queryResult shouldEqual Nil
  }

  it should "set user password" in {
    influx.setUserPassword(userName, userNPass).success.value shouldEqual OkResult
  }

  it should "set privileges" in {
    influx.setPrivileges(userName, userDB, Privileges.READ).success.value shouldEqual OkResult
    influx.setPrivileges("unknown", userDB, Privileges.READ).success.value.ex.get.getMessage shouldEqual "user not found"

    influx.showUserPrivileges(userName).success.value.queryResult shouldEqual Array(UserPrivilegesInfo(userDB, Privileges.READ))
  }

  it should "revoke privileges" in {
    influx.revokePrivileges(userName, userDB, Privileges.READ).success.value shouldEqual OkResult
    influx.showUserPrivileges(userName).success.value.queryResult shouldEqual Array(UserPrivilegesInfo(userDB, Privileges.NO_PRIVILEGES))
  }

  it should "disable admin" in {
    influx.disableAdmin(admin).success.value shouldEqual OkResult
    influx.showUsers.success.value.queryResult.contains(UserInfo(admin, isAdmin = false)) shouldEqual true
  }

  it should "make admin" in {
    influx.makeAdmin(admin).success.value shouldEqual OkResult
    influx.showUsers.success.value.queryResult.contains(UserInfo(admin, isAdmin = true)) shouldEqual true
  }

  it should "drop users" in {
    influx.dropUser(userName).success.value shouldEqual OkResult
    influx.dropUser(admin).success.value shouldEqual OkResult

    influx.close() shouldEqual {}
  }
}

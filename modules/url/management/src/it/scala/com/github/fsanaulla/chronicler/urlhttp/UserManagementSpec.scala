package com.github.fsanaulla.chronicler.urlhttp

import com.github.fsanaulla.chronicler.core.enums.Privileges
import com.github.fsanaulla.chronicler.core.model.{UserInfo, UserPrivilegesInfo}
import com.github.fsanaulla.chronicler.testing.it.{DockerizedInfluxDB, Futures}
import com.github.fsanaulla.chronicler.urlhttp.management.{InfluxMng, UrlManagementClient}
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 10.08.17
  */
class UserManagementSpec extends FlatSpec with Matchers with Futures with DockerizedInfluxDB {

  override def afterAll(): Unit = {
    influx.close()
    super.afterAll()
  }

  val userDB    = "db"
  val userName  = "Martin"
  val userPass  = "pass"
  val userNPass = "new_pass"

  val admin     = "Admin"
  val adminPass = "admin_pass"

  lazy val influx: UrlManagementClient =
    InfluxMng(host, port, Some(creds))

  "User Management API" should "create user" in {
    influx.createDatabase(userDB).get.right.get shouldEqual 200

    influx.createUser(userName, userPass).get.right.get shouldEqual 200
    influx.showUsers.get.right.get.contains(UserInfo(userName, isAdmin = false)) shouldEqual true
  }

  it should "create admin" in {
    influx.createAdmin(admin, adminPass).get.right.get shouldEqual 200
    influx.showUsers.get.right.get.contains(UserInfo(admin, isAdmin = true)) shouldEqual true
  }

  it should "show user privileges" in {
    influx.showUserPrivileges(admin).get.right.get shouldEqual Nil
  }

  it should "set user password" in {
    influx.setUserPassword(userName, userNPass).get.right.get shouldEqual 200
  }

  it should "set privileges" in {
    influx.setPrivileges(userName, userDB, Privileges.READ).get.right.get shouldEqual 200
    influx
      .setPrivileges("unknown", userDB, Privileges.READ)
      .get
      .left
      .get
      .getMessage shouldEqual "user not found"

    influx.showUserPrivileges(userName).get.right.get shouldEqual Array(
      UserPrivilegesInfo(userDB, Privileges.READ)
    )
  }

  it should "revoke privileges" in {
    influx.revokePrivileges(userName, userDB, Privileges.READ).get.right.get shouldEqual 200
    influx.showUserPrivileges(userName).get.right.get shouldEqual Array(
      UserPrivilegesInfo(userDB, Privileges.NO_PRIVILEGES)
    )
  }

  it should "disable admin" in {
    influx.disableAdmin(admin).get.right.get shouldEqual 200
    influx.showUsers.get.right.get.contains(UserInfo(admin, isAdmin = false)) shouldEqual true
  }

  it should "make admin" in {
    influx.makeAdmin(admin).get.right.get shouldEqual 200
    influx.showUsers.get.right.get.contains(UserInfo(admin, isAdmin = true)) shouldEqual true
  }

  it should "drop users" in {
    influx.dropUser(userName).get.right.get shouldEqual 200
    influx.dropUser(admin).get.right.get shouldEqual 200
  }
}

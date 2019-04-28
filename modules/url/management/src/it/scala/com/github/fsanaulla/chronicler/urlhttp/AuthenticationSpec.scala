package com.github.fsanaulla.chronicler.urlhttp

import com.github.fsanaulla.chronicler.core.enums.Privileges
import com.github.fsanaulla.chronicler.core.model.{InfluxException, UserPrivilegesInfo}
import com.github.fsanaulla.chronicler.testing.it.{DockerizedInfluxDB, Futures}
import com.github.fsanaulla.chronicler.urlhttp.management.{InfluxMng, UrlManagementClient}
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 17.08.17
  */
class AuthenticationSpec
  extends FlatSpec
    with Matchers
    with Futures
    with DockerizedInfluxDB {

  val userDB = "db"
  val userName = "some_user"
  val userPass = "some_user_pass"
  val userNPass = "some_new_user_pass"

  val admin = "admin"
  val adminPass = "admin"

  lazy val influx: UrlManagementClient =
    InfluxMng(host, port)

  lazy val authInflux: UrlManagementClient =
    InfluxMng(host = host, port = port, credentials = Some(creds))

  "Authenticated User Management API" should  "create admin user " in {
    influx.showUsers.get.left.get shouldBe a[InfluxException]
  }

  it should "create database" in {
    authInflux.createDatabase(userDB).get.right.get shouldEqual 200
  }
  
  it should "create user" in {
    authInflux.createUser(userName, userPass).get.right.get shouldEqual 200
    authInflux.showUsers.get.right.get.exists(_.username == userName) shouldEqual true
  }

  it should "set user password" in {
    authInflux.setUserPassword(userName, userNPass).get.right.get shouldEqual 200
  }

  it should "set user privileges" in {
    authInflux.setPrivileges(userName, userDB, Privileges.READ).get.right.get shouldEqual 200
  }

  it should "get user privileges" in {
    val userPrivs = authInflux.showUserPrivileges(userName).get.right.get

    userPrivs.length shouldEqual 1
    userPrivs.exists { upi =>
      upi.database == userDB && upi.privilege == Privileges.withName("READ")
    } shouldEqual true
  }

  it should "revoke user privileges" in {
    authInflux.revokePrivileges(userName, userDB, Privileges.READ).get.right.get shouldEqual 200
    authInflux.showUserPrivileges(userName).get.right.get shouldEqual Array(UserPrivilegesInfo(userDB, Privileges.NO_PRIVILEGES))
  }

  it should "drop user" in {
    authInflux.dropUser(userName).get.right.get shouldEqual 200
    authInflux.dropUser(admin).get.right.get shouldEqual 200

    authInflux.close() shouldEqual {}
    influx.close() shouldEqual {}
  }
}

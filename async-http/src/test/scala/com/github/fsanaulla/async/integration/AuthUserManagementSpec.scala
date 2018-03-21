package com.github.fsanaulla.async.integration

import com.github.fsanaulla.chronicler.async.{InfluxAsyncHttpClient, InfluxDB}
import com.github.fsanaulla.core.enums.Privileges
import com.github.fsanaulla.core.model.AuthorizationException
import com.github.fsanaulla.core.test.utils.TestSpec
import com.github.fsanaulla.scalatest.EmbeddedInfluxDB
import org.scalatest.Ignore

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 17.08.17
  */
@Ignore
class AuthUserManagementSpec extends TestSpec with EmbeddedInfluxDB {

  val userDB = "async_not_auth_user_spec_db"
  val userName = "Martin"
  val userPass = "pass"
  val userNPass = "new_pass"

  val admin = "Admin"
  val adminPass = "admin_pass"

  lazy val influx: InfluxAsyncHttpClient = InfluxDB.connect()

  "Not authorized user" should  "not create database" in {
    influx.createDatabase(userDB).futureValue.ex.value shouldBe a[AuthorizationException]
  }

  it should "not create user" in {
    influx.createUser(userName, userPass).futureValue.ex.value shouldBe a[AuthorizationException]
    influx.showUsers().futureValue.ex.value shouldBe a[AuthorizationException]
  }

  it should "not create admin" in {
    influx.createAdmin(admin, adminPass).futureValue.ex.value shouldBe a[AuthorizationException]
    influx.showUsers().futureValue.ex.value shouldBe a[AuthorizationException]
  }

  it should "not get admin privileges" in {
    influx.showUserPrivileges(admin).futureValue.ex.value shouldBe a[AuthorizationException]
  }

  it should "not set user password" in {
    influx.setUserPassword(userName, userNPass).futureValue.ex.value shouldBe a[AuthorizationException]
  }

  it should "not set user privileges" in {
    influx.setPrivileges(userName, userDB, Privileges.READ).futureValue.ex.value shouldBe a[AuthorizationException]
  }

  it should "not get user privileges" in {
    influx.showUserPrivileges(userName).futureValue.ex.value shouldBe a[AuthorizationException]
  }

  it should "not revoke user privileges" in {
    influx.revokePrivileges(userName, userDB, Privileges.READ).futureValue.ex.value shouldBe a[AuthorizationException]
    influx.showUserPrivileges(userName).futureValue.ex.value shouldBe a[AuthorizationException]
  }

  it should "not disable admin" in {
    influx.disableAdmin(admin).futureValue.ex.value shouldBe a[AuthorizationException]
    influx.showUsers().futureValue.ex.value shouldBe a[AuthorizationException]
  }

  it should "not make admin" in {
    influx.makeAdmin(admin).futureValue.ex.value shouldBe a[AuthorizationException]
    influx.showUsers().futureValue.ex.value shouldBe a[AuthorizationException]
  }

  it should "not drop user" in {
    influx.dropUser(userName).futureValue.ex.value shouldBe a[AuthorizationException]
    influx.dropUser(admin).futureValue.ex.value shouldBe a[AuthorizationException]
  }
}

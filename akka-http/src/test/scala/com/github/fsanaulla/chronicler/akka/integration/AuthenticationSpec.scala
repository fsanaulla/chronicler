package com.github.fsanaulla.chronicler.akka.integration

import com.github.fsanaulla.chronicler.akka.{InfluxAkkaHttpClient, InfluxDB}
import com.github.fsanaulla.core.model.AuthorizationException
import com.github.fsanaulla.core.test.utils.ResultMatchers._
import com.github.fsanaulla.core.test.utils.{NonEmptyCredentials, TestSpec}
import com.github.fsanaulla.core.utils.constants.Privileges
import org.scalatest.Ignore

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 17.08.17
  */
@Ignore
// wait for updates from embed-InfluxDB
class AuthenticationSpec extends TestSpec with NonEmptyCredentials {

  val userDB = "not_auth_user_spec_db"
  val userName = "Martin"
  val userPass = "pass"
  val userNPass = "new_pass"

  val admin = "Admin"
  val adminPass = "admin_pass"

  lazy val influx: InfluxAkkaHttpClient = InfluxDB.connect()

  "Not authorized user" should  "not create database" in {
    influx.createAdmin(admin, adminPass).futureValue shouldEqual OkResult
    influx.createDatabase(userDB).futureValue.ex.value shouldBe a[AuthorizationException]
  }

  it should "not create user" in {
    influx.createUser(userName, userPass).futureValue.ex.value shouldBe a[AuthorizationException]
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

  influx.close()
}

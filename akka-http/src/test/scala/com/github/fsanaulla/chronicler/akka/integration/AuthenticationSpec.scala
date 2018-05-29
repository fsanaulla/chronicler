package com.github.fsanaulla.chronicler.akka.integration

import akka.actor.ActorSystem
import akka.testkit.TestKit
import com.github.fsanaulla.chronicler.akka.utils.DockerizedInfluxDB
import com.github.fsanaulla.chronicler.akka.{Influx, InfluxAkkaHttpClient}
import com.github.fsanaulla.core.enums.Privileges
import com.github.fsanaulla.core.model.{AuthorizationException, UserPrivilegesInfo}
import com.github.fsanaulla.core.test.ResultMatchers._
import com.github.fsanaulla.core.test.TestSpec

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 17.08.17
  */
class AuthenticationSpec
  extends TestKit(ActorSystem())
    with TestSpec
    with DockerizedInfluxDB {

  val userDB = "db"
  val userName = "some_user"
  val userPass = "some_user_pass"
  val userNPass = "some_new_user_pass"

  val admin = "admin"
  val adminPass = "admin"

  lazy val influx: InfluxAkkaHttpClient =
    Influx.connect(host, port, None, system)

  lazy val authInflux: InfluxAkkaHttpClient =
    Influx.connect(host = host, port = port, system = system, credentials = Some(creds))

  "AuthenticationUserManagement" should  "create admin user " in {
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
    influx.close()
  }
}

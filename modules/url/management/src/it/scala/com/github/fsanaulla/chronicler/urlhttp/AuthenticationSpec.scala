package com.github.fsanaulla.chronicler.urlhttp

import com.github.fsanaulla.chronicler.core.enums.Privileges
import com.github.fsanaulla.chronicler.core.model.{InfluxException, UserPrivilegesInfo}
import com.github.fsanaulla.chronicler.testing.it.DockerizedInfluxDB
import com.github.fsanaulla.chronicler.urlhttp.management.{InfluxMng, UrlManagementClient}
import org.scalatest.{EitherValues, BeforeAndAfterAll, TryValues}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}

import com.github.fsanaulla.chronicler.testing.BaseSpec

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 17.08.17
  */
class AuthenticationSpec
    extends BaseSpec
    with DockerizedInfluxDB
    with EitherValues
    with TryValues
    with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    influx.close()
    authInflux.close()
    super.afterAll()
  }

  val userDB    = "db"
  val userName  = "some_user"
  val userPass  = "some_user_pass"
  val userNPass = "some_new_user_pass"

  val admin     = "admin"
  val adminPass = "admin"

  lazy val influx: UrlManagementClient = {
    InfluxMng(host, port)
  }

  lazy val authInflux: UrlManagementClient =
    InfluxMng(host, port, Some(credentials))

  "Authenticated User Management API" - {
    "should" - {
      "create admin user " in {
        influx.showUsers.success.value.left.value shouldBe a[InfluxException]
      }

      "create database" in {
        authInflux.createDatabase(userDB).success.value.value shouldEqual 200
      }

      "create user" in {
        authInflux.createUser(userName, userPass).success.value.value shouldEqual 200
        authInflux.showUsers.success.value.value.exists(_.username == userName) shouldEqual true
      }

      "set user password" in {
        authInflux.setUserPassword(userName, userNPass).success.value.value shouldEqual 200
      }

      "set user privileges" in {
        authInflux
          .setPrivileges(userName, userDB, Privileges.READ)
          .success
          .value
          .value shouldEqual 200
      }

      "get user privileges" in {
        val userPrivs = authInflux.showUserPrivileges(userName).success.value.value

        userPrivs.length shouldEqual 1
        userPrivs.exists { upi =>
          upi.database == userDB && upi.privilege == Privileges.withName("READ")
        } shouldEqual true
      }

      "revoke user privileges" in {
        authInflux
          .revokePrivileges(userName, userDB, Privileges.READ)
          .success
          .value
          .value shouldEqual 200
        authInflux.showUserPrivileges(userName).success.value.value shouldEqual Array(
          UserPrivilegesInfo(userDB, Privileges.NO_PRIVILEGES)
        )
      }

      "drop user" in {
        authInflux.dropUser(userName).success.value.value shouldEqual 200
        authInflux.dropUser(admin).success.value.value shouldEqual 200
      }
    }
  }
}

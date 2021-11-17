package com.github.fsanaulla.chronicler.ahc.management

import com.github.fsanaulla.chronicler.core.enums.Privileges
import com.github.fsanaulla.chronicler.core.management.user._
import com.github.fsanaulla.chronicler.core.model.InfluxException
import com.github.fsanaulla.chronicler.testing.BaseSpec
import com.github.fsanaulla.chronicler.testing.it.DockerizedInfluxDB
import org.scalatest.BeforeAndAfterAll
import org.scalatest.EitherValues
import org.scalatest.concurrent.IntegrationPatience
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.ExecutionContext.Implicits.global
import com.github.fsanaulla.chronicler.async.management.InfluxMng

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 17.08.17
  */
class AuthenticationSpec
    extends BaseSpec
    with ScalaFutures
    with EitherValues
    with IntegrationPatience
    with DockerizedInfluxDB
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

  lazy val influx =
    InfluxMng(host, port)

  lazy val authInflux =
    InfluxMng(host = host, port = port, credentials = Some(credentials))

  "Authenticated User Management API" - {

    "should" - {

      "create" - {

        "admin user " in {
          influx.showUsers.futureValue.left.value shouldBe a[InfluxException]
        }

        "database" in {
          authInflux.createDatabase(userDB).futureValue.value shouldEqual 200
        }

        "user" in {
          authInflux.createUser(userName, userPass).futureValue.value shouldEqual 200
          authInflux.showUsers.futureValue.value.exists(_.username == userName) shouldEqual true
        }

      }

      "set" - {
        "user password" in {
          authInflux.setUserPassword(userName, userNPass).futureValue.value shouldEqual 200
        }

        "user privileges" in {
          authInflux
            .setPrivileges(userName, userDB, Privileges.READ)
            .futureValue
            .value shouldEqual 200
        }
      }

      "get user privileges" in {
        val userPrivs = authInflux.showUserPrivileges(userName).futureValue.value

        userPrivs.length shouldEqual 1
        userPrivs.exists { upi =>
          upi.database == userDB && upi.privilege == Privileges.withName("READ")
        } shouldEqual true
      }

      "revoke user privileges" in {
        authInflux
          .revokePrivileges(userName, userDB, Privileges.READ)
          .futureValue
          .value shouldEqual 200

        authInflux.showUserPrivileges(userName).futureValue.value shouldEqual Array(
          UserPrivilegesInfo(userDB, Privileges.NO_PRIVILEGES)
        )
      }

      "drop user" in {
        authInflux.dropUser(userName).futureValue.value shouldEqual 200
        authInflux.dropUser(admin).futureValue.value shouldEqual 200
      }
    }

  }
}

package com.fsanaulla.integration

import com.fsanaulla.InfluxClientsFactory
import com.fsanaulla.model.AuthorizationException
import com.fsanaulla.utils.constants.Privileges
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Second, Seconds, Span}
import org.scalatest.{FlatSpec, Matchers, OptionValues}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 17.08.17
  */
class NotAuthUserManagementSpec extends FlatSpec with Matchers with ScalaFutures with OptionValues {

  val userDB = "not_auth_user_spec_db"
  val userName = "Martin"
  val userPass = "pass"
  val userNPass = "new_pass"

  val admin = "Admin"
  val adminPass = "admin_pass"

  final val influxHost = "localhost"

  implicit val pc = PatienceConfig(Span(20, Seconds), Span(1, Second))

  "not auth user management operation" should "correctly work" in {

    // INIT INFLUX CLIENT
    val influx = InfluxClientsFactory.createHttpClient(influxHost)

    influx.createDatabase(userDB).futureValue.ex.value shouldBe a [AuthorizationException]

    influx.createUser(userName, userPass).futureValue.ex.value shouldBe a [AuthorizationException]
    influx.showUsers().futureValue.ex.value shouldBe a [AuthorizationException]

    influx.createAdmin(admin, adminPass).futureValue.ex.value shouldBe a [AuthorizationException]
    influx.showUsers().futureValue.ex.value shouldBe a [AuthorizationException]

    influx.showUserPrivileges(admin).futureValue.ex.value shouldBe a [AuthorizationException]

    influx.setUserPassword(userName, userNPass).futureValue.ex.value shouldBe a [AuthorizationException]

    influx.setPrivileges(userName, userDB, Privileges.READ).futureValue.ex.value shouldBe a [AuthorizationException]

    influx.showUserPrivileges(userName).futureValue.ex.value shouldBe a [AuthorizationException]

    influx.revokePrivileges(userName, userDB, Privileges.READ).futureValue.ex.value shouldBe a [AuthorizationException]
    influx.showUserPrivileges(userName).futureValue.ex.value shouldBe a [AuthorizationException]

    influx.disableAdmin(admin).futureValue.ex.value shouldBe a [AuthorizationException]
    influx.showUsers().futureValue.ex.value shouldBe a [AuthorizationException]

    influx.makeAdmin(admin).futureValue.ex.value shouldBe a [AuthorizationException]
    influx.showUsers().futureValue.ex.value shouldBe a [AuthorizationException]

    influx.dropUser(userName).futureValue.ex.value shouldBe a [AuthorizationException]
    influx.dropUser(admin).futureValue.ex.value shouldBe a [AuthorizationException]

    influx.showUsers().futureValue.ex.value shouldBe a [AuthorizationException]

    influx.close()
  }
}

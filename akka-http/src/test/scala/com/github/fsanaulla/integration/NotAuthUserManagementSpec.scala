package com.github.fsanaulla.integration

import com.github.fsanaulla.core.model.AuthorizationException
import com.github.fsanaulla.core.utils.constants.Privileges
import com.github.fsanaulla.{InfluxAkkaHttpClient, InfluxClientFactory, TestSpec}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 17.08.17
  */
class NotAuthUserManagementSpec extends TestSpec {

  val userDB = "not_auth_user_spec_db"
  val userName = "Martin"
  val userPass = "pass"
  val userNPass = "new_pass"

  val admin = "Admin"
  val adminPass = "admin_pass"

  "not auth user management operation" should "correctly work" in {

    // INIT INFLUX CLIENT
    val influx = InfluxClientFactory.createHttpClient(influxHost)

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

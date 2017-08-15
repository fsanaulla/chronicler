package com.fsanaulla.integration

import com.fsanaulla.InfluxClient
import com.fsanaulla.model.InfluxImplicits._
import com.fsanaulla.utils.TestHelper.OkResult
import com.fsanaulla.utils.TestSpec
import com.fsanaulla.utils.constants.Privileges
import org.scalatest.OptionValues

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 10.08.17
  */
class UserManagementSpec extends TestSpec with OptionValues {

  val userDB = "user_db"
  val userName = "Martin"
  val userPass = "pass"
  val userNPass = "new_pass"

  val admin = "Admin"
  val adminPass = "admin_pass"

  "User management operation" should "correctly work" in {

    // INIT INFLUX CLIENT
    val influx = InfluxClient(host = influxHost, username = optUser, password = optPassword)

    influx.createDatabase(userDB).futureValue shouldEqual OkResult

    influx.createUser(userName, userPass).futureValue shouldEqual OkResult
    influx.showUsers.futureValue.queryResult.contains(UserInfo(userName, isAdmin = false)) shouldEqual true

    influx.createAdmin(admin, adminPass).futureValue shouldEqual OkResult
    influx.showUsers.futureValue.queryResult.contains(UserInfo(admin, isAdmin = true)) shouldEqual true

    influx.showUserPrivileges(admin).futureValue.queryResult shouldEqual Nil

    influx.setUserPassword(userName, userNPass).futureValue shouldEqual OkResult

    influx.setPrivileges(userName, userDB, Privileges.READ).futureValue shouldEqual OkResult
    influx.setPrivileges("unknown", userDB, Privileges.READ).futureValue.ex.value.getMessage shouldEqual "user not found"
    
    influx.showUserPrivileges(userName).futureValue.queryResult shouldEqual Seq(UserPrivilegesInfo(userDB, Privileges.READ))

    influx.revokePrivileges(userName, userDB, Privileges.READ).futureValue shouldEqual OkResult
    influx.showUserPrivileges(userName).futureValue.queryResult shouldEqual Seq(UserPrivilegesInfo(userDB, Privileges.NO_PRIVILEGES))

    influx.disableAdmin(admin).futureValue shouldEqual OkResult
    influx.showUsers.futureValue.queryResult.contains(UserInfo(admin, isAdmin = false)) shouldEqual true

    influx.makeAdmin(admin).futureValue shouldEqual OkResult
    influx.showUsers.futureValue.queryResult.contains(UserInfo(admin, isAdmin = true)) shouldEqual true

    influx.dropUser(userName).futureValue shouldEqual OkResult
    influx.dropUser(admin).futureValue shouldEqual OkResult

    influx.showUsers.futureValue.queryResult shouldEqual Seq(UserInfo(optUser.get, isAdmin = true))

    influx.close()
  }
}

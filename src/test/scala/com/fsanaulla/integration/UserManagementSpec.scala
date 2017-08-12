package com.fsanaulla.integration

import com.fsanaulla.InfluxClient
import com.fsanaulla.model._
import com.fsanaulla.utils.Extension._
import com.fsanaulla.utils.TestHelper.OkResult
import com.fsanaulla.utils.constants.Privileges

import scala.concurrent.ExecutionContext.Implicits.global


/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 10.08.17
  */
class UserManagementSpec extends IntegrationSpec {

  val userDB = "user_db"
  val userName = "Martin"
  val userPass = "pass"
  val userNPass = "new_pass"

  val admin = "Admin"
  val adminPass = "admin_pass"

  "User management operation" should "correctly work" in {

    // INIT INFLUX CLIENT
    val influx = InfluxClient(host)

    influx.createDatabase(userDB).sync shouldEqual OkResult

    influx.createUser(userName, userPass).sync shouldEqual OkResult
    influx.showUsers.sync.queryResult.contains(UserInfo(userName, isAdmin = false)) shouldEqual true

    influx.createAdmin(admin, adminPass).sync shouldEqual OkResult
    influx.showUsers.sync.queryResult shouldEqual Seq(UserInfo(userName, isAdmin = false), UserInfo(admin, isAdmin = true))

    influx.showUserPrivileges(admin).sync.queryResult shouldEqual Nil

    influx.setUserPassword(userName, userNPass).sync shouldEqual OkResult

    influx.setPrivileges(userName, userDB, Privileges.READ).sync shouldEqual OkResult
    influx.showUserPrivileges(userName).sync.queryResult shouldEqual Seq(UserPrivilegesInfo(userDB, Privileges.READ))

    influx.revokePrivileges(userName, userDB, Privileges.READ).sync shouldEqual OkResult
    influx.showUserPrivileges(userName).sync.queryResult shouldEqual Seq(UserPrivilegesInfo(userDB, Privileges.NO_PRIVILEGES))

    influx.disableAdmin(admin).sync shouldEqual OkResult
    influx.showUsers.sync.queryResult shouldEqual Seq(UserInfo(userName, isAdmin = false), UserInfo(admin, isAdmin = false))

    influx.makeAdmin(admin).sync shouldEqual OkResult
    influx.showUsers.sync.queryResult shouldEqual Seq(UserInfo(userName, isAdmin = false), UserInfo(admin, isAdmin = true))

    influx.dropUser(userName).sync shouldEqual OkResult
    influx.showUsers.sync.queryResult shouldEqual Seq(UserInfo(admin, isAdmin = true))

    influx.close()
  }
}

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

  "User management operation" should "correctly work" in {

    // INIT INFLUX CLIENT
    val influx = InfluxClient(host)

    influx.createDatabase(userDB).sync shouldEqual OkResult

    influx.createUser("Martin", "password").sync shouldEqual OkResult
    influx.showUsers.sync.queryResult.contains(UserInfo("Martin", isAdmin = false)) shouldEqual true

    influx.createAdmin("Admin", "admin_pass").sync shouldEqual OkResult
    influx.showUsers.sync.queryResult shouldEqual Seq(UserInfo("Martin", isAdmin = false), UserInfo("Admin", isAdmin = true))

    influx.showUserPrivileges("Admin").sync.queryResult shouldEqual Nil

    influx.setUserPassword("Martin", "new_password").sync shouldEqual OkResult

    influx.setPrivileges("Martin", "mydb", Privileges.READ).sync shouldEqual OkResult
    influx.showUserPrivileges("Martin").sync.queryResult shouldEqual Seq(UserPrivilegesInfo("mydb", "READ"))

    influx.revokePrivileges("Martin", "mydb", Privileges.READ).sync shouldEqual OkResult
    influx.showUserPrivileges("Martin").sync.queryResult shouldEqual Seq(UserPrivilegesInfo("mydb", "NO PRIVILEGES"))

    influx.disableAdmin("Admin").sync shouldEqual OkResult
    influx.showUsers.sync.queryResult shouldEqual Seq(UserInfo("Martin", isAdmin = false), UserInfo("Admin", isAdmin = false))

    influx.makeAdmin("Admin").sync shouldEqual OkResult
    influx.showUsers.sync.queryResult shouldEqual Seq(UserInfo("Martin", isAdmin = false), UserInfo("Admin", isAdmin = true))

    influx.dropUser("Martin").sync shouldEqual OkResult
    influx.showUsers.sync.queryResult shouldEqual Seq(UserInfo("Admin", isAdmin = true))

    influx.close()
  }
}

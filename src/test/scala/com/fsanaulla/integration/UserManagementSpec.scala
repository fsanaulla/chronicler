package com.fsanaulla.integration

import com.fsanaulla.InfluxClient
import com.fsanaulla.model._
import com.fsanaulla.utils.TestHelper.OkResult
import com.fsanaulla.utils.constants.Privileges
import com.whisk.docker.impl.spotify.DockerKitSpotify
import com.whisk.docker.scalatest.DockerTestKit
import org.scalatest.time.{Second, Seconds, Span}
import org.scalatest.{FlatSpec, Matchers}

class UserManagementSpec
  extends FlatSpec
    with Matchers
    with DockerTestKit
    with DockerKitSpotify
    with DockerInfluxService {

  implicit val pc = PatienceConfig(Span(20, Seconds), Span(1, Second))

  "Influx container" should "get up and run correctly" in {
    // CHECKING CONTAINER
    isContainerReady(influxdbContainer).futureValue shouldBe true
    influxdbContainer.getPorts().futureValue.get(dockerPort) should not be None
    influxdbContainer.getIpAddresses().futureValue should not be Seq.empty
  }

  "User management operation" should "correctly work" in {

    // INIT INFLUX CLIENT
    val influx = InfluxClient(influxdbContainer.getIpAddresses().futureValue.head, dockerPort)

    influx.createDatabase("mydb").futureValue shouldEqual OkResult

    influx.createUser("Martin", "password").futureValue shouldEqual OkResult
    influx.showUsers.futureValue.queryResult shouldEqual Seq(UserInfo("Martin", isAdmin = false))

    influx.createAdmin("Admin", "admin_pass").futureValue shouldEqual OkResult
    influx.showUsers.futureValue.queryResult shouldEqual Seq(UserInfo("Martin", isAdmin = false), UserInfo("Admin", isAdmin = true))

    influx.showUserPrivileges("Admin").futureValue.queryResult shouldEqual Nil

    influx.setUserPassword("Martin", "new_password").futureValue shouldEqual OkResult

    influx.setPrivileges("Martin", "mydb", Privileges.READ).futureValue shouldEqual OkResult
    influx.showUserPrivileges("Martin").futureValue.queryResult shouldEqual Seq(UserPrivilegesInfo("mydb", "READ"))

    influx.revokePrivileges("Martin", "mydb", Privileges.READ).futureValue shouldEqual OkResult
    influx.showUserPrivileges("Martin").futureValue.queryResult shouldEqual Seq(UserPrivilegesInfo("mydb", "NO PRIVILEGES"))

    influx.disableAdmin("Admin").futureValue shouldEqual OkResult
    influx.showUsers.futureValue.queryResult shouldEqual Seq(UserInfo("Martin", isAdmin = false), UserInfo("Admin", isAdmin = false))

    influx.makeAdmin("Admin").futureValue shouldEqual OkResult
    influx.showUsers.futureValue.queryResult shouldEqual Seq(UserInfo("Martin", isAdmin = false), UserInfo("Admin", isAdmin = true))

    influx.dropUser("Martin").futureValue shouldEqual OkResult
    influx.showUsers.futureValue.queryResult shouldEqual Seq(UserInfo("Admin", isAdmin = true))

    influx.close()
  }
}

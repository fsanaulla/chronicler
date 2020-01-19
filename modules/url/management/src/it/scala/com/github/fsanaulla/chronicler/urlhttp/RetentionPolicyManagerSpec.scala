package com.github.fsanaulla.chronicler.urlhttp

import com.github.fsanaulla.chronicler.core.duration._
import com.github.fsanaulla.chronicler.core.model.RetentionPolicyInfo
import com.github.fsanaulla.chronicler.testing.it.{DockerizedInfluxDB, Futures}
import com.github.fsanaulla.chronicler.urlhttp.management.{InfluxMng, UrlManagementClient}
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.07.17
  */
class RetentionPolicyManagerSpec
  extends FlatSpec
  with Matchers
  with Futures
  with DockerizedInfluxDB {

  override def afterAll(): Unit = {
    influx.close()
    super.afterAll()
  }

  val rpDB = "db"

  lazy val influx: UrlManagementClient =
    InfluxMng(s"http://$host", port, Some(creds))

  "Retention policy API" should "create retention policy" in {
    influx.createDatabase(rpDB).get.right.get shouldEqual 200

    influx.showDatabases().get.right.get.contains(rpDB) shouldEqual true

    influx
      .createRetentionPolicy("test", rpDB, 2.hours, 2, Some(2.hours), default = true)
      .get
      .right
      .get shouldEqual 200

    influx
      .showRetentionPolicies(rpDB)
      .get
      .right
      .get
      .contains(RetentionPolicyInfo("test", "2h0m0s", "2h0m0s", 2, default = true)) shouldEqual true

  }

  it should "drop retention policy" in {
    influx.dropRetentionPolicy("autogen", rpDB).get.right.get shouldEqual 200

    influx.showRetentionPolicies(rpDB).get.right.get shouldEqual Seq(
      RetentionPolicyInfo("test", "2h0m0s", "2h0m0s", 2, default = true)
    )
  }

  it should "update retention policy" in {
    influx.updateRetentionPolicy("test", rpDB, Some(3.hours)).get.right.get shouldEqual 200

    influx.showRetentionPolicies(rpDB).get.right.get shouldEqual Seq(
      RetentionPolicyInfo("test", "3h0m0s", "2h0m0s", 2, default = true)
    )
  }

  it should "clean up everything" in {
    influx.dropRetentionPolicy("test", rpDB).get.right.get shouldEqual 200

    influx.showRetentionPolicies(rpDB).get.right.get.toList shouldEqual Nil

    influx.dropDatabase(rpDB).get.right.get shouldEqual 200

    influx.showDatabases().get.right.get.contains(rpDB) shouldEqual false
  }
}

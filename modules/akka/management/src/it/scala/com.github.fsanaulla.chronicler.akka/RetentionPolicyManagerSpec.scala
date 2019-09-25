package com.github.fsanaulla.chronicler.akka

import _root_.akka.actor.ActorSystem
import _root_.akka.testkit.TestKit
import com.github.fsanaulla.chronicler.akka.management.{AkkaManagementClient, InfluxMng}
import com.github.fsanaulla.chronicler.core.duration._
import com.github.fsanaulla.chronicler.core.model.RetentionPolicyInfo
import com.github.fsanaulla.chronicler.testing.it.{DockerizedInfluxDB, Futures}
import org.scalatest.{FlatSpecLike, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.07.17
  */
class RetentionPolicyManagerSpec
  extends TestKit(ActorSystem())
  with FlatSpecLike
  with Matchers
  with Futures
  with DockerizedInfluxDB {

  override def afterAll(): Unit = {
    super.afterAll()
    TestKit.shutdownActorSystem(system)
  }

  val rpDB = "db"

  lazy val influx: AkkaManagementClient =
    InfluxMng(host, port, Some(creds))

  "Retention policy API" should "create retention policy" in {
    influx.createDatabase(rpDB).futureValue.right.get shouldEqual 200

    influx.showDatabases().futureValue.right.get.contains(rpDB) shouldEqual true

    influx
      .createRetentionPolicy("test", rpDB, 2 hours, 2, Some(2 hours), default = true)
      .futureValue
      .right
      .get shouldEqual 200

    influx
      .showRetentionPolicies(rpDB)
      .futureValue
      .right
      .get
      .contains(RetentionPolicyInfo("test", "2h0m0s", "2h0m0s", 2, default = true)) shouldEqual true

  }

  it should "drop retention policy" in {
    influx.dropRetentionPolicy("autogen", rpDB).futureValue.right.get shouldEqual 200

    influx.showRetentionPolicies(rpDB).futureValue.right.get shouldEqual Seq(
      RetentionPolicyInfo("test", "2h0m0s", "2h0m0s", 2, default = true)
    )
  }

  it should "update retention policy" in {
    influx.updateRetentionPolicy("test", rpDB, Some(3 hours)).futureValue.right.get shouldEqual 200

    influx.showRetentionPolicies(rpDB).futureValue.right.get shouldEqual Seq(
      RetentionPolicyInfo("test", "3h0m0s", "2h0m0s", 2, default = true)
    )
  }

  it should "clean up everything" in {
    influx.dropRetentionPolicy("test", rpDB).futureValue.right.get shouldEqual 200

    influx.showRetentionPolicies(rpDB).futureValue.right.get.toList shouldEqual Nil

    influx.dropDatabase(rpDB).futureValue.right.get shouldEqual 200

    influx.showDatabases().futureValue.right.get.contains(rpDB) shouldEqual false
  }

  it should "clear up after all" in {
    influx.close() shouldEqual {}
  }
}

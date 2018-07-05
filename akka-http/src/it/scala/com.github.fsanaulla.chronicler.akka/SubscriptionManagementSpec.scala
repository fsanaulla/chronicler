package com.github.fsanaulla.chronicler.akka

import _root_.akka.actor.ActorSystem
import _root_.akka.testkit.TestKit
import com.github.fsanaulla.chronicler.core.enums.{Destination, Destinations}
import com.github.fsanaulla.chronicler.core.model.Subscription
import com.github.fsanaulla.chronicler.core.utils.InfluxDuration._
import com.github.fsanaulla.chronicler.testing.it.ResultMatchers._
import com.github.fsanaulla.chronicler.testing.it.{DockerizedInfluxDB, Futures}
import com.github.fsanaulla.chronicler.testing.unit.FlatSpecWithMatchers

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 21.08.17
  */
class SubscriptionManagementSpec
  extends TestKit(ActorSystem())
    with FlatSpecWithMatchers
    with Futures
    with DockerizedInfluxDB {

  val subName = "subs"
  val dbName = "async_subs_spec_db"
  val rpName = "subs_rp"
  val destType: Destination = Destinations.ANY
  val newDestType: Destination = Destinations.ALL
  val hosts = Seq("udp://h1.example.com:9090", "udp://h2.example.com:9090")
  val subscription = Subscription(rpName, subName, destType, hosts)
  val newSubscription: Subscription = subscription.copy(destType = newDestType)

  val duration: String = 1.hours + 30.minutes

  lazy val influx: InfluxAkkaHttpClient =
    Influx(host, port, Some(creds))

  "Subscription operation" should "create subscription" in {

    influx.createDatabase(dbName).futureValue shouldEqual OkResult

    influx.createRetentionPolicy(rpName, dbName, duration, 1, Some(duration)).futureValue shouldEqual OkResult

    influx.showDatabases().futureValue.queryResult.contains(dbName) shouldEqual true

    influx.createSubscription(subName, dbName, rpName, destType, hosts).futureValue shouldEqual OkResult

    influx.showSubscriptionsInfo.futureValue.queryResult.head.subscriptions shouldEqual Array(subscription)
  }


  it should "drop subscription" in {
    influx.dropSubscription(subName, dbName, rpName).futureValue shouldEqual OkResult

    influx.showSubscriptionsInfo.futureValue.queryResult shouldEqual Nil

    influx.dropRetentionPolicy(rpName, dbName).futureValue shouldEqual OkResult

    influx.dropDatabase(dbName).futureValue shouldEqual OkResult

    influx.close() shouldEqual {}
  }
}

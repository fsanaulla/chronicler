package com.github.fsanaulla.chronicler.akka.integration

import com.github.fsanaulla.chronicler.akka.InfluxClientFactory
import com.github.fsanaulla.core.model.Subscription
import com.github.fsanaulla.core.test.utils.ResultMatchers._
import com.github.fsanaulla.core.test.utils.{EmptyCredentials, TestSpec}
import com.github.fsanaulla.core.utils.InfluxDuration._
import com.github.fsanaulla.core.utils.constants.Destinations
import com.github.fsanaulla.scalatest.EmbeddedInfluxDB

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 21.08.17
  */
class SubscriptionManagementSpec
  extends TestSpec
    with EmptyCredentials
    with EmbeddedInfluxDB {

  override def httpPort = 9001
  override def backUpPort: Int = httpPort + 1

  val subName = "subs"
  val dbName = "subs_spec_db"
  val rpName = "subs_rp"
  val destType: Destinations.ANY.type = Destinations.ANY
  val newDestType: Destinations.ALL.type = Destinations.ALL
  val hosts = Seq("udp://h1.example.com:9090", "udp://h2.example.com:9090")
  val subscription = Subscription(rpName, subName, destType, hosts)
  val newSubscription: Subscription = subscription.copy(destType = newDestType)

  val duration: String = 1.hours + 30.minutes

  lazy val influx = InfluxClientFactory.createHttpClient(
    host = influxHost,
    port = httpPort,
    username = credentials.username,
    password = credentials.password)

  "Subs operation" should "create subscription" in {

    influx.createDatabase(dbName).futureValue shouldEqual OkResult

    influx.createRetentionPolicy(rpName, dbName, duration, 1, Some(duration)).futureValue shouldEqual OkResult

    influx.showDatabases().futureValue.queryResult.contains(dbName) shouldEqual true

    influx.createSubscription(subName, dbName, rpName, destType, hosts).futureValue shouldEqual OkResult

    influx.showSubscriptions(dbName).futureValue.queryResult shouldEqual Seq(subscription)

  }

  it should "update subscription" in {
    influx.updateSubscription(subName, dbName, rpName, newDestType, hosts).futureValue shouldEqual OkResult

    influx.showSubscriptions(dbName).futureValue.queryResult shouldEqual Seq(newSubscription)
  }

  it should "drop subscription" in {

    influx.dropSubscription(subName, dbName, rpName).futureValue shouldEqual OkResult

    influx.showSubscriptions(dbName).futureValue.queryResult shouldEqual Nil

    influx.close() shouldEqual {}
  }

}

package com.github.fsanaulla.async.integration

import com.github.fsanaulla.async.utils.TestHelper._
import com.github.fsanaulla.chronicler.async.{InfluxAsyncHttpClient, InfluxDB}
import com.github.fsanaulla.core.enums.{Destination, Destinations}
import com.github.fsanaulla.core.model.Subscription
import com.github.fsanaulla.core.test.TestSpec
import com.github.fsanaulla.core.testing.configurations.InfluxHTTPConf
import com.github.fsanaulla.core.utils.InfluxDuration._
import com.github.fsanaulla.scalatest.EmbeddedInfluxDB

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 21.08.17
  */
class SubscriptionManagementSpec extends TestSpec with EmbeddedInfluxDB with InfluxHTTPConf {

  val subName = "subs"
  val dbName = "async_subs_spec_db"
  val rpName = "subs_rp"
  val destType: Destination = Destinations.ANY
  val newDestType: Destination = Destinations.ALL
  val hosts = Array("udp://h1.example.com:9090", "udp://h2.example.com:9090")
  val subscription = Subscription(rpName, subName, destType, hosts)
  val newSubscription: Subscription = subscription.copy(destType = newDestType)

  val duration: String = 1.hours + 30.minutes

  lazy val influx: InfluxAsyncHttpClient = InfluxDB.connect()

  "Subscription operation" should "create subscription" in {

    influx.createDatabase(dbName).futureValue shouldEqual OkResult

    influx.createRetentionPolicy(rpName, dbName, duration, 1, Some(duration)).futureValue shouldEqual OkResult

    influx.showDatabases().futureValue.queryResult.contains(dbName) shouldEqual true

    influx.createSubscription(subName, dbName, rpName, destType, hosts).futureValue shouldEqual OkResult

    influx.showSubscriptions(dbName).futureValue.queryResult shouldEqual Array(subscription)
  }

  it should "update subscriptions" in {
    influx.updateSubscription(subName, dbName, rpName, newDestType, hosts).futureValue shouldEqual OkResult

    influx.showSubscriptions(dbName).futureValue.queryResult shouldEqual Array(newSubscription)
  }

  it should "drop subscription" in {
    influx.dropSubscription(subName, dbName, rpName).futureValue shouldEqual OkResult

    influx.showSubscriptions(dbName).futureValue.queryResult shouldEqual Nil

    influx.dropRetentionPolicy(rpName, dbName).futureValue shouldEqual OkResult

    influx.dropDatabase(dbName).futureValue shouldEqual OkResult

    influx.close() shouldEqual {}
  }
}

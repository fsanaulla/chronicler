package com.github.fsanaulla.async.integration

import com.github.fsanaulla.async.utils.TestHelper._
import com.github.fsanaulla.chronicler.async.{InfluxAsyncHttpClient, InfluxClientFactory}
import com.github.fsanaulla.core.model.Subscription
import com.github.fsanaulla.core.test.utils.TestSpec
import com.github.fsanaulla.core.utils.InfluxDuration._
import com.github.fsanaulla.core.utils.constants.Destinations
import com.github.fsanaulla.core.utils.constants.Destinations.Destination

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 21.08.17
  */
class SubscriptionManagementSpec extends TestSpec {

  val subName = "subs"
  val dbName = "async_subs_spec_db"
  val rpName = "subs_rp"
  val destType: Destination = Destinations.ANY
  val newDestType: Destination = Destinations.ALL
  val hosts = Seq("udp://h1.example.com:9090", "udp://h2.example.com:9090")
  val subscription = Subscription(rpName, subName, destType, hosts)
  val newSubscription: Subscription = subscription.copy(destType = newDestType)

  val duration: String = 1.hours + 30.minutes

  lazy val influx: InfluxAsyncHttpClient = InfluxClientFactory.createHttpClient(
    host = influxHost,
    username = credentials.username,
    password = credentials.password)

  "Subscription operation" should "create subscription" in {

    influx.createDatabase(dbName).futureValue shouldEqual OkResult

    influx.createRetentionPolicy(rpName, dbName, duration, 1, Some(duration)).futureValue shouldEqual OkResult

    influx.showDatabases().futureValue.queryResult.contains(dbName) shouldEqual true

    influx.createSubscription(subName, dbName, rpName, destType, hosts).futureValue shouldEqual OkResult

    influx.showSubscriptions(dbName).futureValue.queryResult shouldEqual Seq(subscription)
  }

  it should "update subscriptions" in {
    influx.updateSubscription(subName, dbName, rpName, newDestType, hosts).futureValue shouldEqual OkResult

    influx.showSubscriptions(dbName).futureValue.queryResult shouldEqual Seq(newSubscription)
  }

  it should "drop subscription" in {
    influx.dropSubscription(subName, dbName, rpName).futureValue shouldEqual OkResult

    influx.showSubscriptions(dbName).futureValue.queryResult shouldEqual Nil

    influx.dropRetentionPolicy(rpName, dbName).futureValue shouldEqual OkResult

    influx.dropDatabase(dbName).futureValue shouldEqual OkResult

  }

  it should "clear up after all" in {
    influx.dropDatabase(dbName).futureValue shouldEqual OkResult

    influx.close() shouldEqual {}
  }
}

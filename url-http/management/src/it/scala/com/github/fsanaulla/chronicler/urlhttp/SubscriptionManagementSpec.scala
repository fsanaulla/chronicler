package com.github.fsanaulla.chronicler.urlhttp

import com.github.fsanaulla.chronicler.core.enums.{Destination, Destinations}
import com.github.fsanaulla.chronicler.core.model.Subscription
import com.github.fsanaulla.chronicler.core.utils.InfluxDuration._
import com.github.fsanaulla.chronicler.testing.it.DockerizedInfluxDB
import com.github.fsanaulla.chronicler.testing.it.ResultMatchers.OkResult
import com.github.fsanaulla.chronicler.testing.unit.FlatSpecWithMatchers
import com.github.fsanaulla.chronicler.urlhttp.management.{Influx, UrlManagementClient}
import org.scalatest.TryValues

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 21.08.17
  */
class SubscriptionManagementSpec extends FlatSpecWithMatchers with DockerizedInfluxDB with TryValues {

  val subName = "subs"
  val dbName = "async_subs_spec_db"
  val rpName = "subs_rp"
  val destType: Destination = Destinations.ANY
  val newDestType: Destination = Destinations.ALL
  val hosts = Array("udp://h1.example.com:9090", "udp://h2.example.com:9090")
  val subscription = Subscription(rpName, subName, destType, hosts)
  val newSubscription: Subscription = subscription.copy(destType = newDestType)

  val duration: String = 1.hours + 30.minutes

  lazy val influx: UrlManagementClient =
    Influx.management(host, port, Some(creds))

  "Subscription operation" should "create subscription" in {

    influx.createDatabase(dbName).success.value shouldEqual OkResult

    influx.createRetentionPolicy(rpName, dbName, duration, 1, Some(duration)).success.value shouldEqual OkResult

    influx.showDatabases().success.value.queryResult.contains(dbName) shouldEqual true

    influx.createSubscription(subName, dbName, rpName, destType, hosts).success.value shouldEqual OkResult

    influx.showSubscriptionsInfo.success.value.queryResult.head.subscriptions shouldEqual Array(subscription)
  }

  it should "drop subscription" in {
    influx.dropSubscription(subName, dbName, rpName).success.value shouldEqual OkResult

    influx.dropRetentionPolicy(rpName, dbName).success.value shouldEqual OkResult

    influx.dropDatabase(dbName).success.value shouldEqual OkResult

    influx.close() shouldEqual {}
  }
}

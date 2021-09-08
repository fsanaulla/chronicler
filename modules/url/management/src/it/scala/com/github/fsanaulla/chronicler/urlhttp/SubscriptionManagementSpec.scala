package com.github.fsanaulla.chronicler.urlhttp

import com.github.fsanaulla.chronicler.core.duration._
import com.github.fsanaulla.chronicler.core.enums.{Destination, Destinations}
import com.github.fsanaulla.chronicler.core.management.subscription.Subscription
import com.github.fsanaulla.chronicler.testing.it.DockerizedInfluxDB
import com.github.fsanaulla.chronicler.urlhttp.management.{InfluxMng, UrlManagementClient}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{EitherValues, TryValues, BeforeAndAfterAll}
import com.github.fsanaulla.chronicler.testing.BaseSpec

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 21.08.17
  */
class SubscriptionManagementSpec
    extends BaseSpec
    with EitherValues
    with TryValues
    with DockerizedInfluxDB
    with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    influx.close()
    super.afterAll()
  }

  val subName                       = "subs"
  val dbName                        = "async_subs_spec_db"
  val rpName                        = "subs_rp"
  val destType: Destination         = Destinations.ANY
  val newDestType: Destination      = Destinations.ALL
  val hosts: Array[String]          = Array("udp://h1.example.com:9090", "udp://h2.example.com:9090")
  val subscription: Subscription    = Subscription(rpName, subName, destType, hosts)
  val newSubscription: Subscription = subscription.copy(destType = newDestType)

  val duration: String = 1.hours + 30.minutes

  lazy val influx: UrlManagementClient =
    InfluxMng(host, port, Some(credentials))

  "Subscription API" - {
    
    "should" - {

      "create subscription" in {

        influx.createDatabase(dbName).success.value.value shouldEqual 200

        influx
          .createRetentionPolicy(rpName, dbName, duration, 1, Some(duration))
          .success
          .value
          .value shouldEqual 200

        influx.showDatabases().success.value.value.contains(dbName) shouldEqual true

        influx
          .createSubscription(subName, dbName, rpName, destType, hosts)
          .success
          .value
          .value shouldEqual 200

        val Some(subscr) = influx.showSubscriptionsInfo.success.value.value.headOption
          .flatMap(_.subscriptions.headOption)

        subscr.subsName shouldEqual subscription.subsName
        subscr.addresses shouldEqual subscription.addresses
        subscr.destType shouldEqual subscription.destType
        subscr.addresses.toList shouldEqual subscription.addresses.toList
      }

      "drop subscription" in {
        influx.dropSubscription(subName, dbName, rpName).success.value.value shouldEqual 200

        influx.showSubscriptionsInfo.success.value.value shouldEqual Nil

        influx.dropRetentionPolicy(rpName, dbName).success.value.value shouldEqual 200

        influx.dropDatabase(dbName).success.value.value shouldEqual 200
      }
    }
  }
}

package com.github.fsanaulla.chronicler.ahc.management

import com.github.fsanaulla.chronicler.core.duration._
import com.github.fsanaulla.chronicler.core.enums.{Destination, Destinations}
import com.github.fsanaulla.chronicler.core.model.Subscription
import com.github.fsanaulla.chronicler.testing.it.{DockerizedInfluxDB, Futures}
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 21.08.17
  */
class SubscriptionManagementSpec
  extends FlatSpec
  with Matchers
  with Futures
  with DockerizedInfluxDB {

  override def afterAll(): Unit = {
    influx.close()
    super.afterAll()
  }

  val subName                       = "subs"
  val dbName                        = "async_subs_spec_db"
  val rpName                        = "subs_rp"
  val destType: Destination         = Destinations.ANY
  val newDestType: Destination      = Destinations.ALL
  val hosts                         = Array("udp://h1.example.com:9090", "udp://h2.example.com:9090")
  val subscription                  = Subscription(rpName, subName, destType, hosts)
  val newSubscription: Subscription = subscription.copy(destType = newDestType)

  val duration: String = 1.hours + 30.minutes

  lazy val influx: AhcManagementClient =
    InfluxMng(host, port, Some(creds))

  "Subscription API" should "create subscription" in {

    influx.createDatabase(dbName).futureValue.right.get shouldEqual 200

    influx
      .createRetentionPolicy(rpName, dbName, duration, 1, Some(duration))
      .futureValue
      .right
      .get shouldEqual 200

    influx.showDatabases().futureValue.right.get.contains(dbName) shouldEqual true

    influx
      .createSubscription(subName, dbName, rpName, destType, hosts)
      .futureValue
      .right
      .get shouldEqual 200

    val subscr = influx.showSubscriptionsInfo.futureValue.right.get.headOption
      .flatMap(_.subscriptions.headOption)
      .get

    subscr.subsName shouldEqual subscription.subsName
    subscr.addresses shouldEqual subscription.addresses
    subscr.destType shouldEqual subscription.destType
    subscr.addresses.toList shouldEqual subscription.addresses.toList
  }

  it should "drop subscription" in {
    influx.dropSubscription(subName, dbName, rpName).futureValue.right.get shouldEqual 200

    influx.showSubscriptionsInfo.futureValue.right.get shouldEqual Nil

    influx.dropRetentionPolicy(rpName, dbName).futureValue.right.get shouldEqual 200

    influx.dropDatabase(dbName).futureValue.right.get shouldEqual 200
  }
}

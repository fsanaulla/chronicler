package com.github.fsanaulla.chronicler.async.unit

import com.github.fsanaulla.chronicler.async.handlers.AsyncQueryHandler
import com.github.fsanaulla.chronicler.async.utils.TestHelper._
import com.github.fsanaulla.chronicler.core.enums.Destinations
import com.github.fsanaulla.chronicler.core.query.SubscriptionsManagementQuery
import com.github.fsanaulla.chronicler.testing.{EmptyCredentials, FlatSpecWithMatchers, NonEmptyCredentials}
import com.softwaremill.sttp.Uri

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 21.08.17
  */
class SubscriptionsManagementQuerySpec extends FlatSpecWithMatchers {

  trait Env extends AsyncQueryHandler with SubscriptionsManagementQuery[Uri] {
    val host = "localhost"
    val port = 8086
  }
  trait AuthEnv extends Env with NonEmptyCredentials
  trait NonAuthEnv extends Env with EmptyCredentials

  val subName = "subs"
  val dbName = "db"
  val rpName = "rp"
  val destType: Destinations.ANY.type = Destinations.ANY
  val hosts: Seq[String] = Seq("host1", "host2")
  val resHosts: String = Seq("host1", "host2").map(str => s"'$str'").mkString(", ")

  val createRes = s"CREATE SUBSCRIPTION $subName ON $dbName.$rpName DESTINATIONS $destType $resHosts"

  "SubscriptionsManagementQuery" should "create subs query" in new AuthEnv {
    createSubscriptionQuery(subName, dbName, rpName, destType, hosts).toString() shouldEqual
      queryTesterAuth(createRes)(credentials.get)
  }

  it should "create subs query without auth" in new NonAuthEnv {
    createSubscriptionQuery(subName, dbName, rpName, destType, hosts).toString() shouldEqual
      queryTester(createRes)
  }

  val dropRes = s"DROP SUBSCRIPTION $subName ON $dbName.$rpName"

  it should "drop subs query" in new AuthEnv {
    dropSubscriptionQuery(subName, dbName, rpName).toString() shouldEqual
      queryTesterAuth(dropRes)(credentials.get)
  }

  it should "drop subs query without auth" in new NonAuthEnv {
    dropSubscriptionQuery(subName, dbName, rpName).toString() shouldEqual queryTester(dropRes)
  }

  val showRes = "SHOW SUBSCRIPTIONS"

  it should "show subs query" in new AuthEnv {
    showSubscriptionsQuery().toString() shouldEqual
      queryTesterAuth(showRes)(credentials.get)
  }

  it should "show subs query without auth" in new NonAuthEnv {
    showSubscriptionsQuery().toString() shouldEqual queryTester(showRes)
  }
}

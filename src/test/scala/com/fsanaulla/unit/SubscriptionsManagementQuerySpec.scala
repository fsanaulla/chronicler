package com.fsanaulla.unit

import com.fsanaulla.query.SubscriptionsManagementQuery
import com.fsanaulla.utils.TestHelper._
import com.fsanaulla.utils.TestSpec
import com.fsanaulla.utils.constants.Destinations

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 21.08.17
  */
class SubscriptionsManagementQuerySpec extends TestSpec with SubscriptionsManagementQuery {

  val subName = "subs"
  val dbName = "db"
  val rpName = "rp"
  val destType  = Destinations.ANY
  val hosts = Seq("host1", "host2")
  val resHosts = Seq("host1", "host2").map(str => s"'$str'").mkString(", ")

  "create subs query" should "correctly work" in {
    val createRes = s"CREATE SUBSCRIPTION $subName ON $dbName.$rpName DESTINATIONS $destType $resHosts"

    createSubscriptionQuery(subName, dbName, rpName, destType, hosts) shouldEqual queryTesterAuth(createRes)

    createSubscriptionQuery(subName, dbName, rpName, destType, hosts)(emptyCredentials) shouldEqual queryTester(createRes)
  }

  "drop subs query" should "correctly work" in {
    val dropRes = s"DROP SUBSCRIPTION $subName ON $dbName.$rpName"

    dropSubscriptionQuery(subName, dbName, rpName) shouldEqual queryTesterAuth(dropRes)

    dropSubscriptionQuery(subName, dbName, rpName)(emptyCredentials) shouldEqual queryTester(dropRes)
  }

  "show subs query" should "correctly work" in {
    val showRes = "SHOW SUBSCRIPTIONS"

    showSubscriptionsQuery() shouldEqual queryTesterAuth(showRes)

    showSubscriptionsQuery()(emptyCredentials) shouldEqual queryTester(showRes)
  }
}

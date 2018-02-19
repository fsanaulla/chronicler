package com.github.fsanaulla.unit

import akka.http.scaladsl.model.Uri
import com.github.fsanaulla.core.query.SubscriptionsManagementQuery
import com.github.fsanaulla.core.test.utils.TestSpec
import com.github.fsanaulla.core.utils.constants.Destinations
import com.github.fsanaulla.handlers.AkkaQueryHandler
import com.github.fsanaulla.utils.TestHelper._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 21.08.17
  */
class SubscriptionsManagementQuerySpec
  extends TestSpec
    with AkkaQueryHandler
    with SubscriptionsManagementQuery[Uri] {

  val subName = "subs"
  val dbName = "db"
  val rpName = "rp"
  val destType: Destinations.ANY.type = Destinations.ANY
  val hosts: Seq[String] = Seq("host1", "host2")
  val resHosts: String = Seq("host1", "host2").map(str => s"'$str'").mkString(", ")

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

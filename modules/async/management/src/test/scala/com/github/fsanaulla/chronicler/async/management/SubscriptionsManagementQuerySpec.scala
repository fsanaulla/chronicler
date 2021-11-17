/*
 * Copyright 2017-2019 Faiaz Sanaulla
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.fsanaulla.chronicler.async.management

import com.github.fsanaulla.chronicler.core.enums.Destinations
import com.github.fsanaulla.chronicler.core.query.SubscriptionsManagementQuery
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import sttp.model.Uri
import com.github.fsanaulla.chronicler.async.shared.AsyncQueryBuilder
import com.github.fsanaulla.chronicler.async.shared.AsyncQueryBuilder

/** Created by Author: fayaz.sanaulla@gmail.com Date: 21.08.17
  */
class SubscriptionsManagementQuerySpec
    extends AnyFlatSpec
    with Matchers
    with SubscriptionsManagementQuery[Uri] {

  val subName                         = "subs"
  val dbName                          = "db"
  val rpName                          = "rp"
  val destType: Destinations.ANY.type = Destinations.ANY
  val hosts: Seq[String]              = Seq("host1", "host2")
  val resHosts: String                = Seq("host1", "host2").map(str => s"'$str'").mkString(", ")
  implicit val qb                     = new AsyncQueryBuilder("localhost", 8086)

  val createRes =
    s"CREATE SUBSCRIPTION $subName ON $dbName.$rpName DESTINATIONS $destType $resHosts"

  it should "create subs query" in {
    createSubscriptionQuery(subName, dbName, rpName, destType, hosts).toString shouldEqual
      queryTester(createRes)
  }

  it should "create subs query without auth" in {
    createSubscriptionQuery(subName, dbName, rpName, destType, hosts).toString shouldEqual
      queryTester(createRes)
  }

  val dropRes = s"DROP SUBSCRIPTION $subName ON $dbName.$rpName"

  it should "drop subs query" in {
    dropSubscriptionQuery(subName, dbName, rpName).toString shouldEqual
      queryTester(dropRes)
  }

  it should "drop subs query without auth" in {
    dropSubscriptionQuery(subName, dbName, rpName).toString shouldEqual queryTester(dropRes)
  }

  val showRes = "SHOW SUBSCRIPTIONS"

  it should "show subs query" in {
    showSubscriptionsQuery.toString shouldEqual
      queryTester(showRes)
  }

  it should "show subs query without auth" in {
    showSubscriptionsQuery.toString shouldEqual queryTester(showRes)
  }
}

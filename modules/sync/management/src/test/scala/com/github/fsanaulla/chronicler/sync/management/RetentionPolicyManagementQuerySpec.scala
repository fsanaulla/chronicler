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

package com.github.fsanaulla.chronicler.sync.management

import com.github.fsanaulla.chronicler.core.duration._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.language.postfixOps
import com.github.fsanaulla.chronicler.core.management.rp.RetentionPolicyManagementQuery
import sttp.model.Uri
import com.github.fsanaulla.chronicler.sync.shared.SyncQueryBuilder

/** Created by Author: fayaz.sanaulla@gmail.com Date: 27.07.17
  */
class RetentionPolicyManagementQuerySpec
    extends AnyFlatSpec
    with Matchers
    with RetentionPolicyManagementQuery[Uri] {

  implicit val qb = new SyncQueryBuilder("localhost", 8086)

  val testRPName = "testRP"
  val testDBName = "testDB"

  it should "create retention policy" in {
    createRPQuery(
      testRPName,
      testDBName,
      4 hours,
      3,
      Some(4 hours),
      default = true
    ).toString shouldEqual
      queryTester(
        s"CREATE RETENTION POLICY $testRPName ON $testDBName DURATION 4h REPLICATION 3 SHARD DURATION 4h DEFAULT"
      )

    createRPQuery(testRPName, testDBName, 4 hours, 3, None, default = true).toString shouldEqual
      queryTester(
        s"CREATE RETENTION POLICY $testRPName ON $testDBName DURATION 4h REPLICATION 3 DEFAULT"
      )

    createRPQuery(testRPName, testDBName, 4 hours, 3, Some(4 hours)).toString shouldEqual
      queryTester(
        s"CREATE RETENTION POLICY $testRPName ON $testDBName DURATION 4h REPLICATION 3 SHARD DURATION 4h"
      )
  }

  it should "drop retention policy" in {
    dropRPQuery(testRPName, testDBName).toString shouldEqual
      queryTester(s"DROP RETENTION POLICY $testRPName ON $testDBName")
  }

  it should "update retention policy" in {
    updateRPQuery(
      testRPName,
      testDBName,
      Some(4 hours),
      Some(3),
      Some(4 hours),
      default = true
    ).toString shouldEqual
      queryTester(
        s"ALTER RETENTION POLICY $testRPName ON $testDBName DURATION 4h REPLICATION 3 SHARD DURATION 4h DEFAULT"
      )

    updateRPQuery(testRPName, testDBName, Some(4 hours), Some(3), None).toString shouldEqual
      queryTester(
        s"ALTER RETENTION POLICY $testRPName ON $testDBName DURATION 4h REPLICATION 3"
      )

  }
}

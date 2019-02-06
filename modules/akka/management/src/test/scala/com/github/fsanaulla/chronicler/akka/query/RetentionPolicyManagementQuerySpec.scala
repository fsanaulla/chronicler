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

package com.github.fsanaulla.chronicler.akka.query

import _root_.akka.http.scaladsl.model.Uri
import com.github.fsanaulla.chronicler.akka.TestHelper._
import com.github.fsanaulla.chronicler.akka.shared.handlers.AkkaQueryBuilder
import com.github.fsanaulla.chronicler.core.duration._
import com.github.fsanaulla.chronicler.core.model.HasCredentials
import com.github.fsanaulla.chronicler.core.query.RetentionPolicyManagementQuery
import com.github.fsanaulla.chronicler.testing.unit.{EmptyCredentials, FlatSpecWithMatchers, NonEmptyCredentials}

import scala.language.postfixOps

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.07.17
  */
class RetentionPolicyManagementQuerySpec extends FlatSpecWithMatchers {

  trait Env extends AkkaQueryBuilder with RetentionPolicyManagementQuery[Uri] { self: HasCredentials =>
    val host = "localhost"
    val port = 8086
  }
  trait AuthEnv extends Env with NonEmptyCredentials
  trait NonAuthEnv extends Env with EmptyCredentials

  val testRPName = "testRP"
  val testDBName = "testDB"

  "RetentionPolicyManagement" should "create retention policy" in new AuthEnv {
    createRetentionPolicyQuery(testRPName, testDBName, 4 hours, 3, Some(4 hours), default = true) shouldEqual
      queryTesterAuth(s"CREATE RETENTION POLICY $testRPName ON $testDBName DURATION 4h REPLICATION 3 SHARD DURATION 4h DEFAULT")(credentials.get)

    createRetentionPolicyQuery(testRPName, testDBName, 4 hours, 3, None, default = true) shouldEqual
      queryTesterAuth(s"CREATE RETENTION POLICY $testRPName ON $testDBName DURATION 4h REPLICATION 3 DEFAULT")(credentials.get)

    createRetentionPolicyQuery(testRPName, testDBName, 4 hours, 3, Some(4 hours)) shouldEqual
      queryTesterAuth(s"CREATE RETENTION POLICY $testRPName ON $testDBName DURATION 4h REPLICATION 3 SHARD DURATION 4h")(credentials.get)
  }

  it should "create retention policy without auth" in new NonAuthEnv {
    createRetentionPolicyQuery(testRPName, testDBName, 4 hours, 3, None) shouldEqual
      queryTester(s"CREATE RETENTION POLICY $testRPName ON $testDBName DURATION 4h REPLICATION 3")
  }

  it should "drop retention policy" in new AuthEnv {
    dropRetentionPolicyQuery(testRPName, testDBName) shouldEqual
      queryTesterAuth(s"DROP RETENTION POLICY $testRPName ON $testDBName")(credentials.get)
  }

  it should "drop retention policy without auth" in new NonAuthEnv {
    dropRetentionPolicyQuery(testRPName, testDBName) shouldEqual
      queryTester(s"DROP RETENTION POLICY $testRPName ON $testDBName")
  }

  it should "update retention policy" in new AuthEnv {
    updateRetentionPolicyQuery(testRPName, testDBName, Some(4 hours), Some(3), Some(4 hours), default = true) shouldEqual
      queryTesterAuth(s"ALTER RETENTION POLICY $testRPName ON $testDBName DURATION 4h REPLICATION 3 SHARD DURATION 4h DEFAULT")(credentials.get)

    updateRetentionPolicyQuery(testRPName, testDBName, Some(4 hours), Some(3), None) shouldEqual
      queryTesterAuth(s"ALTER RETENTION POLICY $testRPName ON $testDBName DURATION 4h REPLICATION 3")(credentials.get)

  }

  it should "update retention policy without auth" in new NonAuthEnv {
    updateRetentionPolicyQuery(testRPName, testDBName, Some(4 hours), None, None) shouldEqual
      queryTester(s"ALTER RETENTION POLICY $testRPName ON $testDBName DURATION 4h")
    updateRetentionPolicyQuery(testRPName, testDBName, None, Some(3), Some(4 hours)) shouldEqual
      queryTester(s"ALTER RETENTION POLICY $testRPName ON $testDBName REPLICATION 3 SHARD DURATION 4h")
  }
}

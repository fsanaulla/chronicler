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

import com.github.fsanaulla.chronicler.core.management.db.DataManagementQuery
import org.scalatest.matchers.should.Matchers
import com.github.fsanaulla.chronicler.async.management._
import com.github.fsanaulla.chronicler.async.shared.AsyncQueryBuilder
import sttp.model.Uri
import org.scalatest.flatspec.AnyFlatSpec

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.07.17
  */
class DataManagementQuerySpec extends AnyFlatSpec with Matchers with DataManagementQuery[Uri] {

  val testDb: String                  = "testDb"
  val testSeries: String              = "testSeries"
  val testMeasurement: String         = "testMeasurement"
  val testShardId: Int                = 1
  val testWhereClause: Option[String] = Some("bag > 4")
  val testLimit: Option[Int]          = Some(4)
  val testOffset: Option[Int]         = Some(3)

  implicit val qb = new AsyncQueryBuilder("localhost", 8086)

  it should "generate correct 'create database' query" in {
    createDatabaseQuery(testDb, None, None, None, None).toString shouldEqual
      queryTester(s"CREATE DATABASE $testDb")

    createDatabaseQuery(testDb, None, Some(2), None, None).toString shouldEqual
      queryTester(s"CREATE DATABASE $testDb WITH REPLICATION 2")
  }

  it should "generate correct 'drop database' query" in {
    dropDatabaseQuery(testDb).toString shouldEqual
      queryTester(s"DROP DATABASE $testDb")
  }

  it should "generate correct 'drop series' query" in {
    dropSeriesQuery(testDb, testSeries).toString shouldEqual
      queryTester(testDb, s"DROP SERIES FROM $testSeries")
  }

  it should "generate  correct 'drop measurement' query" in {
    dropMeasurementQuery(testDb, testMeasurement).toString shouldEqual
      queryTester(testDb, s"DROP MEASUREMENT $testMeasurement")
  }

  it should "generate correct 'drop all series' query" in {
    deleteAllSeriesQuery(testDb, testSeries).toString shouldEqual
      queryTester(testDb, s"DELETE FROM $testSeries")
  }

  it should "generate correct 'show measurement' query" in {
    showMeasurementQuery(testDb).toString shouldEqual
      queryTester(testDb, "SHOW MEASUREMENTS")
  }

  it should "generate correct 'show database' query" in {
    showDatabasesQuery.toString shouldEqual
      queryTester(s"SHOW DATABASES")
  }

  it should "generate correct 'show tag-key' query" in {
    showTagKeysQuery(testDb, testMeasurement, testWhereClause, testLimit, testOffset).toString shouldEqual
      queryTester(
        s"SHOW TAG KEYS ON $testDb FROM $testMeasurement WHERE ${testWhereClause.get} LIMIT ${testLimit.get} OFFSET ${testOffset.get}"
      )

    showTagKeysQuery(testDb, testMeasurement, testWhereClause, None, None).toString shouldEqual
      queryTester(
        s"SHOW TAG KEYS ON $testDb FROM $testMeasurement WHERE ${testWhereClause.get}"
      )
  }

  it should "generate correct 'show tag-value' query" in {
    showTagValuesQuery(testDb, testMeasurement, Seq("key"), testWhereClause, testLimit, testOffset).toString shouldEqual
      queryTester(
        s"SHOW TAG VALUES ON $testDb FROM $testMeasurement WITH KEY = key WHERE ${testWhereClause.get} LIMIT ${testLimit.get} OFFSET ${testOffset.get}"
      )
    showTagValuesQuery(
      testDb,
      testMeasurement,
      Seq("key", "key1"),
      testWhereClause,
      testLimit,
      testOffset
    ).toString shouldEqual
      queryTester(
        s"SHOW TAG VALUES ON $testDb FROM $testMeasurement WITH KEY IN (key,key1) WHERE ${testWhereClause.get} LIMIT ${testLimit.get} OFFSET ${testOffset.get}"
      )
  }

  it should "generate correct 'show field-key' query" in {
    showFieldKeysQuery(testDb, testMeasurement).toString shouldEqual
      queryTester(s"SHOW FIELD KEYS ON $testDb FROM $testMeasurement")
  }

  it should "generate correct 'create database' query without auth" in {
    createDatabaseQuery(
      testDb,
      Some("3d"),
      None,
      None,
      None
    ).toString shouldEqual queryTester(s"CREATE DATABASE $testDb WITH DURATION 3d")

    createDatabaseQuery(
      testDb,
      Some("3d"),
      Some(2),
      Some("1d"),
      Some("testName")
    ).toString shouldEqual queryTester(
      s"CREATE DATABASE $testDb WITH DURATION 3d REPLICATION 2 SHARD DURATION 1d NAME testName"
    )
  }

  it should "generate correct 'drop database' query without auth" in {
    dropDatabaseQuery(testDb).toString shouldEqual queryTester(s"DROP DATABASE $testDb")
  }

  it should "generate correct 'drop series' query without auth" in {
    dropSeriesQuery(testDb, testSeries).toString shouldEqual queryTester(
      testDb,
      s"DROP SERIES FROM $testSeries"
    )
  }

  it should "generate auth correct 'drop measurement' query without auth" in {
    dropMeasurementQuery(testDb, testMeasurement).toString shouldEqual
      queryTester(testDb, s"DROP MEASUREMENT $testMeasurement")
  }

  it should "generate correct auth 'drop all series' query without auth" in {
    deleteAllSeriesQuery(testDb, testSeries).toString shouldEqual queryTester(
      testDb,
      s"DELETE FROM $testSeries"
    )
  }

  it should "generate correct auth 'show measurement' query without auth" in {
    showMeasurementQuery(testDb).toString shouldEqual queryTester(testDb, "SHOW MEASUREMENTS")
  }

  it should "generate correct 'show database' query without auth" in {
    showDatabasesQuery.toString shouldEqual queryTester(s"SHOW DATABASES")
  }

  it should "generate correct 'show tag-key' query without auth" in {
    showTagKeysQuery(
      testDb,
      testMeasurement,
      None,
      None,
      None
    ).toString shouldEqual queryTester(s"SHOW TAG KEYS ON $testDb FROM $testMeasurement")

    showTagKeysQuery(
      testDb,
      testMeasurement,
      testWhereClause,
      testLimit,
      None
    ).toString shouldEqual queryTester(
      s"SHOW TAG KEYS ON $testDb FROM $testMeasurement WHERE ${testWhereClause.get} LIMIT ${testLimit.get}"
    )
  }

  it should "generate correct 'show tag-value' query without auth" in {
    showTagValuesQuery(testDb, testMeasurement, Seq("key"), None, None, None).toString shouldEqual queryTester(
      s"SHOW TAG VALUES ON $testDb FROM $testMeasurement WITH KEY = key"
    )
    showTagValuesQuery(testDb, testMeasurement, Seq("key", "key1"), testWhereClause, None, None).toString shouldEqual queryTester(
      s"SHOW TAG VALUES ON $testDb FROM $testMeasurement WITH KEY IN (key,key1) WHERE ${testWhereClause.get}"
    )
  }

  it should "generate correct 'show field-key' query without auth" in {
    showFieldKeysQuery(testDb, testMeasurement).toString shouldEqual
      queryTester(s"SHOW FIELD KEYS ON $testDb FROM $testMeasurement")
  }
}

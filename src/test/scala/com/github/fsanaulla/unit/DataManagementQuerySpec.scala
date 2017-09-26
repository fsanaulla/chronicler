package com.github.fsanaulla.unit

import com.github.fsanaulla.query.DataManagementQuery
import com.github.fsanaulla.utils.TestCredentials
import com.github.fsanaulla.utils.TestHelper._
import org.scalatest.{FlatSpecLike, Matchers}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.07.17
  */
class DataManagementQuerySpec
  extends DataManagementQuery
    with FlatSpecLike
    with Matchers
    with TestCredentials {

  val testDb: String = "testDb"
  val testSeries: String = "testSeries"
  val testMeasurement: String = "testMeasurement"
  val testShardId: Int = 1
  val testWhereClause = Some("bag > 4")
  val testLimit = Some(4)
  val testOffset = Some(3)

  "DatabaseManagementQuerys" should "generate correct 'create database' query" in {

    createDatabaseQuery(testDb, None, None, None, None) shouldEqual queryTesterAuth(s"CREATE DATABASE $testDb")

    createDatabaseQuery(testDb, Some("3d"), None, None, None)(emptyCredentials) shouldEqual queryTester(s"CREATE DATABASE $testDb WITH DURATION 3d")

    createDatabaseQuery(testDb, None, Some(2), None, None) shouldEqual queryTesterAuth(s"CREATE DATABASE $testDb WITH REPLICATION 2")

    createDatabaseQuery(testDb, Some("3d"), Some(2), Some("1d"), Some("testName"))(emptyCredentials) shouldEqual queryTester(s"CREATE DATABASE $testDb WITH DURATION 3d REPLICATION 2 SHARD DURATION 1d NAME testName")
  }

  it should "generate correct 'drop database' query" in {
    dropDatabaseQuery(testDb) shouldEqual queryTesterAuth(s"DROP DATABASE $testDb")

    dropDatabaseQuery(testDb)(emptyCredentials) shouldEqual queryTester(s"DROP DATABASE $testDb")
  }

  it should "generate correct 'drop series' query" in {
    dropSeriesQuery(testDb, testSeries) shouldEqual queryTesterAuth(testDb, s"DROP SERIES FROM $testSeries")

    dropSeriesQuery(testDb, testSeries)(emptyCredentials) shouldEqual queryTester(testDb, s"DROP SERIES FROM $testSeries")
  }

  it should "generate correct 'drop measurement' query" in {
    dropMeasurementQuery(testDb, testMeasurement) shouldEqual queryTesterAuth(testDb, s"DROP MEASUREMENT $testMeasurement")

    dropMeasurementQuery(testDb, testMeasurement)(emptyCredentials) shouldEqual queryTester(testDb, s"DROP MEASUREMENT $testMeasurement")
  }

  it should "generate correct 'drop all series' query" in {
    deleteAllSeriesQuery(testDb, testSeries) shouldEqual queryTesterAuth(testDb, s"DELETE FROM $testSeries")

    deleteAllSeriesQuery(testDb, testSeries)(emptyCredentials) shouldEqual queryTester(testDb, s"DELETE FROM $testSeries")
  }


  it should "generate correct 'show measurement' query" in {
    showMeasurementQuery(testDb) shouldEqual queryTesterAuth(testDb, "SHOW MEASUREMENTS")

    showMeasurementQuery(testDb)(emptyCredentials) shouldEqual queryTester(testDb, "SHOW MEASUREMENTS")
  }

  it should "generate correct 'show database' query" in {
    showDatabasesQuery() shouldEqual queryTesterAuth(s"SHOW DATABASES")

    showDatabasesQuery()(emptyCredentials) shouldEqual queryTester(s"SHOW DATABASES")
  }

  it should "generate correct 'show tag-key' query" in {
    showTagKeysQuery(testDb, testMeasurement, testWhereClause, testLimit, testOffset) shouldEqual queryTesterAuth(s"SHOW TAG KEYS ON $testDb FROM $testMeasurement WHERE ${testWhereClause.get} LIMIT ${testLimit.get} OFFSET ${testOffset.get}")

    showTagKeysQuery(testDb, testMeasurement, None, None, None)(emptyCredentials) shouldEqual queryTester(s"SHOW TAG KEYS ON $testDb FROM $testMeasurement")

    showTagKeysQuery(testDb, testMeasurement, testWhereClause, None, None) shouldEqual queryTesterAuth(s"SHOW TAG KEYS ON $testDb FROM $testMeasurement WHERE ${testWhereClause.get}")

    showTagKeysQuery(testDb, testMeasurement, testWhereClause, testLimit, None)(emptyCredentials) shouldEqual queryTester(s"SHOW TAG KEYS ON $testDb FROM $testMeasurement WHERE ${testWhereClause.get} LIMIT ${testLimit.get}")
  }

  it should "generate correct 'show tag-value' query" in {
    showTagValuesQuery(testDb, testMeasurement, Seq("key"), testWhereClause, testLimit, testOffset) shouldEqual queryTesterAuth(s"SHOW TAG VALUES ON $testDb FROM $testMeasurement WITH KEY = key WHERE ${testWhereClause.get} LIMIT ${testLimit.get} OFFSET ${testOffset.get}")

    showTagValuesQuery(testDb, testMeasurement, Seq("key", "key1"), testWhereClause, testLimit, testOffset) shouldEqual queryTesterAuth(s"SHOW TAG VALUES ON $testDb FROM $testMeasurement WITH KEY IN (key,key1) WHERE ${testWhereClause.get} LIMIT ${testLimit.get} OFFSET ${testOffset.get}")

    showTagValuesQuery(testDb, testMeasurement, Seq("key"), None, None, None)(emptyCredentials) shouldEqual queryTester(s"SHOW TAG VALUES ON $testDb FROM $testMeasurement WITH KEY = key")

    showTagValuesQuery(testDb, testMeasurement, Seq("key", "key1"), testWhereClause, None, None)(emptyCredentials) shouldEqual queryTester(s"SHOW TAG VALUES ON $testDb FROM $testMeasurement WITH KEY IN (key,key1) WHERE ${testWhereClause.get}")
  }

  it should "generate correct 'show field-key' query" in {
    showFieldKeysQuery(testDb, testMeasurement) shouldEqual queryTesterAuth(s"SHOW FIELD KEYS ON $testDb FROM $testMeasurement")

    showFieldKeysQuery(testDb, testMeasurement)(emptyCredentials) shouldEqual queryTester(s"SHOW FIELD KEYS ON $testDb FROM $testMeasurement")
  }

}

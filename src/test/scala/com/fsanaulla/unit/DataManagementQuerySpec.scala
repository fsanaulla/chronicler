package com.fsanaulla.unit

import com.fsanaulla.query.DataManagementQuery
import com.fsanaulla.utils.TestCredentials
import com.fsanaulla.utils.TestHelper._
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

  "create database query" should "return correct query" in {

    createDatabaseQuery(testDb, None, None, None, None) shouldEqual queryTesterAuth(s"CREATE DATABASE $testDb")

    createDatabaseQuery(testDb, Some("3d"), None, None, None)(emptyCredentials) shouldEqual queryTester(s"CREATE DATABASE $testDb WITH DURATION 3d")

    createDatabaseQuery(testDb, None, Some(2), None, None) shouldEqual queryTesterAuth(s"CREATE DATABASE $testDb WITH REPLICATION 2")

    createDatabaseQuery(testDb, Some("3d"), Some(2), Some("1d"), Some("testName"))(emptyCredentials) shouldEqual queryTester(s"CREATE DATABASE $testDb WITH DURATION 3d REPLICATION 2 SHARD DURATION 1d NAME testName")
  }

  "drop database query" should "return correct query" in {
    dropDatabaseQuery(testDb) shouldEqual queryTesterAuth(s"DROP DATABASE $testDb")

    dropDatabaseQuery(testDb)(emptyCredentials) shouldEqual queryTester(s"DROP DATABASE $testDb")
  }

  "drop series query" should "return correct query" in {
    dropSeriesQuery(testDb, testSeries) shouldEqual queryTesterAuth(testDb, s"DROP SERIES FROM $testSeries")

    dropSeriesQuery(testDb, testSeries)(emptyCredentials) shouldEqual queryTester(testDb, s"DROP SERIES FROM $testSeries")
  }

  "drop measurement query" should "return correct query" in {
    dropMeasurementQuery(testDb, testMeasurement) shouldEqual queryTesterAuth(testDb, s"DROP MEASUREMENT $testMeasurement")

    dropMeasurementQuery(testDb, testMeasurement)(emptyCredentials) shouldEqual queryTester(testDb, s"DROP MEASUREMENT $testMeasurement")
  }

  "delete series query" should "return correct query" in {
    deleteAllSeriesQuery(testDb, testSeries) shouldEqual queryTesterAuth(testDb, s"DELETE FROM $testSeries")

    deleteAllSeriesQuery(testDb, testSeries)(emptyCredentials) shouldEqual queryTester(testDb, s"DELETE FROM $testSeries")
  }


  "show measurements query" should "return correct query" in {
    showMeasurementQuery(testDb) shouldEqual queryTesterAuth(testDb, "SHOW MEASUREMENTS")

    showMeasurementQuery(testDb)(emptyCredentials) shouldEqual queryTester(testDb, "SHOW MEASUREMENTS")
  }

  "show databases query" should "return correct query" in {
    showDatabasesQuery() shouldEqual queryTesterAuth(s"SHOW DATABASES")

    showDatabasesQuery()(emptyCredentials) shouldEqual queryTester(s"SHOW DATABASES")
  }

  "show tag keys query" should "return correct query" in {
    showTagKeysQuery(testDb, testMeasurement, testWhereClause, testLimit, testOffset) shouldEqual queryTesterAuth(s"SHOW TAG KEYS ON $testDb FROM $testMeasurement WHERE ${testWhereClause.get} LIMIT ${testLimit.get} OFFSET ${testOffset.get}")

    showTagKeysQuery(testDb, testMeasurement, None, None, None)(emptyCredentials) shouldEqual queryTester(s"SHOW TAG KEYS ON $testDb FROM $testMeasurement")

    showTagKeysQuery(testDb, testMeasurement, testWhereClause, None, None) shouldEqual queryTesterAuth(s"SHOW TAG KEYS ON $testDb FROM $testMeasurement WHERE ${testWhereClause.get}")

    showTagKeysQuery(testDb, testMeasurement, testWhereClause, testLimit, None)(emptyCredentials) shouldEqual queryTester(s"SHOW TAG KEYS ON $testDb FROM $testMeasurement WHERE ${testWhereClause.get} LIMIT ${testLimit.get}")
  }

  "show tag value query" should "return correct query" in {
    showTagValuesQuery(testDb, testMeasurement, Seq("key"), testWhereClause, testLimit, testOffset) shouldEqual queryTesterAuth(s"SHOW TAG VALUES ON $testDb FROM $testMeasurement WITH KEY = key WHERE ${testWhereClause.get} LIMIT ${testLimit.get} OFFSET ${testOffset.get}")

    showTagValuesQuery(testDb, testMeasurement, Seq("key", "key1"), testWhereClause, testLimit, testOffset) shouldEqual queryTesterAuth(s"SHOW TAG VALUES ON $testDb FROM $testMeasurement WITH KEY IN (key,key1) WHERE ${testWhereClause.get} LIMIT ${testLimit.get} OFFSET ${testOffset.get}")

    showTagValuesQuery(testDb, testMeasurement, Seq("key"), None, None, None)(emptyCredentials) shouldEqual queryTester(s"SHOW TAG VALUES ON $testDb FROM $testMeasurement WITH KEY = key")

    showTagValuesQuery(testDb, testMeasurement, Seq("key", "key1"), testWhereClause, None, None)(emptyCredentials) shouldEqual queryTester(s"SHOW TAG VALUES ON $testDb FROM $testMeasurement WITH KEY IN (key,key1) WHERE ${testWhereClause.get}")



  }

  "show field keys query" should "return correct query" in {
    showFieldKeysQuery(testDb, testMeasurement) shouldEqual queryTesterAuth(s"SHOW FIELD KEYS ON $testDb FROM $testMeasurement")

    showFieldKeysQuery(testDb, testMeasurement)(emptyCredentials) shouldEqual queryTester(s"SHOW FIELD KEYS ON $testDb FROM $testMeasurement")
  }

}

package com.fsanaulla.unit.management.data

import com.fsanaulla.Helper.queryTester
import com.fsanaulla.query.DataManagementQuery
import org.scalatest.{FlatSpecLike, Matchers}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.07.17
  */
class DataManagementQuerySpec
  extends DataManagementQuery
    with FlatSpecLike
    with Matchers {

  val testDb: String = "testDb"
  val testSeries: String = "testSeries"
  val testMeasurement: String = "testMeasurement"
  val testShardId: Int = 1

  "create database query" should "return correct query" in {

    createDatabaseQuery(testDb, None, None, None, None) shouldEqual queryTester(s"CREATE+DATABASE+$testDb")

    createDatabaseQuery(testDb, Some("3d"), None, None, None) shouldEqual queryTester(s"CREATE+DATABASE+$testDb+WITH+DURATION+3d")

    createDatabaseQuery(testDb, None, Some("replication"), None, None) shouldEqual queryTester(s"CREATE+DATABASE+$testDb+WITH+REPLICATION+replication")

    createDatabaseQuery(testDb, Some("3d"), Some("repl"), Some("1d"), Some("testName")) shouldEqual queryTester(s"CREATE+DATABASE+$testDb+WITH+DURATION+3d+REPLICATION+repl+SHARD+DURATION+1d+NAME+testName")
  }

  "drop database query" should "return correct query" in {
    dropDatabaseQuery(testDb) shouldEqual queryTester(s"DROP+DATABASE+$testDb")
  }

  "drop series query" should "return correct query" in {
    dropSeriesQuery(testDb, testSeries) shouldEqual queryTester(testDb, s"DROP+SERIES+FROM+$testSeries")
  }

  "drop measurement query" should "return correct query" in {
    dropMeasurementQuery(testDb, testMeasurement) shouldEqual queryTester(testDb, s"DROP+MEASUREMENT+$testMeasurement")
  }

  "delete series query" should "return correct query" in {
    deleteAllSeriesQuery(testDb, testSeries) shouldEqual queryTester(testDb, s"DELETE+FROM+$testSeries")
  }

  "drop shard query" should "return correct query" in {
    dropShardQuery(testShardId) shouldEqual queryTester(s"DROP+SHARD+$testShardId")
  }

  "show measurements query" should "return correct query" in {
    showMeasurementQuery(testDb) shouldEqual queryTester(testDb, "SHOW+MEASUREMENTS")
  }
  "show databases query" should "return correct query" in {
    showDatabasesQuery() shouldEqual queryTester(s"SHOW+DATABASES")
  }

}

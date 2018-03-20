package com.github.fsanaulla.async.unit

import com.github.fsanaulla.async.utils.TestHelper._
import com.github.fsanaulla.chronicler.async.handlers.AsyncQueryHandler
import com.github.fsanaulla.core.query.DataManagementQuery
import com.github.fsanaulla.core.test.utils.{EmptyCredentials, FlatSpecWithMatchers, NonEmptyCredentials}
import com.softwaremill.sttp.Uri

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.07.17
  */
class DataManagementQuerySpec extends FlatSpecWithMatchers {

  trait Env extends AsyncQueryHandler with DataManagementQuery[Uri] {
    val host = "localhost"
    val port = 8086
  }
  trait AuthEnv extends Env with NonEmptyCredentials
  trait NonAuthEnv extends Env with EmptyCredentials

  val testDb: String = "testDb"
  val testSeries: String = "testSeries"
  val testMeasurement: String = "testMeasurement"
  val testShardId: Int = 1
  val testWhereClause = Some("bag > 4")
  val testLimit = Some(4)
  val testOffset = Some(3)

  "DatabaseManagementQuerys" should "generate correct 'create database' query" in new AuthEnv {
    createDatabaseQuery(testDb, None, None, None, None).toString() shouldEqual
      queryTesterAuth(s"CREATE DATABASE $testDb")(credentials.get)
    createDatabaseQuery(testDb, None, Some(2), None, None).toString() shouldEqual
      queryTesterAuth(s"CREATE DATABASE $testDb WITH REPLICATION 2")(credentials.get)
  }

  it should "generate correct 'drop database' query" in new AuthEnv {
    dropDatabaseQuery(testDb).toString() shouldEqual
      queryTesterAuth(s"DROP DATABASE $testDb")(credentials.get)
  }

  it should "generate correct 'drop series' query" in new AuthEnv {
    dropSeriesQuery(testDb, testSeries).toString() shouldEqual
      queryTesterAuth(testDb, s"DROP SERIES FROM $testSeries")(credentials.get)
  }

  it should "generate  correct 'drop measurement' query" in new AuthEnv {
    dropMeasurementQuery(testDb, testMeasurement).toString() shouldEqual
      queryTesterAuth(testDb, s"DROP MEASUREMENT $testMeasurement")(credentials.get)
  }

  it should "generate correct 'drop all series' query" in new AuthEnv {
    deleteAllSeriesQuery(testDb, testSeries).toString() shouldEqual
      queryTesterAuth(testDb, s"DELETE FROM $testSeries")(credentials.get)
  }

  it should "generate correct 'show measurement' query" in new AuthEnv {
    showMeasurementQuery(testDb).toString() shouldEqual
      queryTesterAuth(testDb, "SHOW MEASUREMENTS")(credentials.get)
  }

  it should "generate correct 'show database' query" in new AuthEnv {
    showDatabasesQuery().toString() shouldEqual
      queryTesterAuth(s"SHOW DATABASES")(credentials.get)
  }

  it should "generate correct 'show tag-key' query" in new AuthEnv {
    showTagKeysQuery(testDb, testMeasurement, testWhereClause, testLimit, testOffset).toString() shouldEqual
      queryTesterAuth(s"SHOW TAG KEYS ON $testDb FROM $testMeasurement WHERE ${testWhereClause.get} LIMIT ${testLimit.get} OFFSET ${testOffset.get}")(credentials.get)

    showTagKeysQuery(testDb, testMeasurement, testWhereClause, None, None).toString() shouldEqual
      queryTesterAuth(s"SHOW TAG KEYS ON $testDb FROM $testMeasurement WHERE ${testWhereClause.get}")(credentials.get)
  }

  it should "generate correct 'show tag-value' query" in new AuthEnv {
    showTagValuesQuery(testDb, testMeasurement, Seq("key"), testWhereClause, testLimit, testOffset).toString() shouldEqual
      queryTesterAuth(s"SHOW TAG VALUES ON $testDb FROM $testMeasurement WITH KEY = key WHERE ${testWhereClause.get} LIMIT ${testLimit.get} OFFSET ${testOffset.get}")(credentials.get)
    showTagValuesQuery(testDb, testMeasurement, Seq("key", "key1"), testWhereClause, testLimit, testOffset).toString() shouldEqual
      queryTesterAuth(s"SHOW TAG VALUES ON $testDb FROM $testMeasurement WITH KEY IN (key,key1) WHERE ${testWhereClause.get} LIMIT ${testLimit.get} OFFSET ${testOffset.get}")(credentials.get)
  }

  it should "generate correct 'show field-key' query" in new AuthEnv {
    showFieldKeysQuery(testDb, testMeasurement).toString() shouldEqual
      queryTesterAuth(s"SHOW FIELD KEYS ON $testDb FROM $testMeasurement")(credentials.get)
  }

  it should "generate correct 'create database' query without auth" in new NonAuthEnv {
    createDatabaseQuery(testDb, Some("3d"), None, None, None).toString() shouldEqual
      queryTester(s"CREATE DATABASE $testDb WITH DURATION 3d")

    createDatabaseQuery(testDb, Some("3d"), Some(2), Some("1d"), Some("testName")).toString() shouldEqual
      queryTester(s"CREATE DATABASE $testDb WITH DURATION 3d REPLICATION 2 SHARD DURATION 1d NAME testName")
  }

  it should "generate correct 'drop database' query without auth" in new NonAuthEnv {
    dropDatabaseQuery(testDb).toString() shouldEqual queryTester(s"DROP DATABASE $testDb")
  }

  it should "generate correct 'drop series' query without auth" in new NonAuthEnv {
    dropSeriesQuery(testDb, testSeries).toString() shouldEqual queryTester(testDb, s"DROP SERIES FROM $testSeries")
  }

  it should "generate auth correct 'drop measurement' query without auth" in new NonAuthEnv {
    dropMeasurementQuery(testDb, testMeasurement).toString() shouldEqual
      queryTester(testDb, s"DROP MEASUREMENT $testMeasurement")
  }

  it should "generate correct auth 'drop all series' query without auth" in new NonAuthEnv {
    deleteAllSeriesQuery(testDb, testSeries).toString() shouldEqual queryTester(testDb, s"DELETE FROM $testSeries")
  }

  it should "generate correct auth 'show measurement' query without auth" in new NonAuthEnv {
    showMeasurementQuery(testDb).toString() shouldEqual queryTester(testDb, "SHOW MEASUREMENTS")
  }

  it should "generate correct 'show database' query without auth" in new NonAuthEnv {
    showDatabasesQuery().toString() shouldEqual queryTester(s"SHOW DATABASES")
  }

  it should "generate correct 'show tag-key' query without auth" in new NonAuthEnv {
    showTagKeysQuery(testDb, testMeasurement, None, None, None).toString() shouldEqual
      queryTester(s"SHOW TAG KEYS ON $testDb FROM $testMeasurement")
    showTagKeysQuery(testDb, testMeasurement, testWhereClause, testLimit, None).toString() shouldEqual
      queryTester(s"SHOW TAG KEYS ON $testDb FROM $testMeasurement WHERE ${testWhereClause.get} LIMIT ${testLimit.get}")
  }

  it should "generate correct 'show tag-value' query without auth" in new NonAuthEnv {
    showTagValuesQuery(testDb, testMeasurement, Seq("key"), None, None, None).toString() shouldEqual queryTester(s"SHOW TAG VALUES ON $testDb FROM $testMeasurement WITH KEY = key")
    showTagValuesQuery(testDb, testMeasurement, Seq("key", "key1"), testWhereClause, None, None).toString() shouldEqual queryTester(s"SHOW TAG VALUES ON $testDb FROM $testMeasurement WITH KEY IN (key,key1) WHERE ${testWhereClause.get}")
  }

  it should "generate correct 'show field-key' query without auth" in new NonAuthEnv {
    showFieldKeysQuery(testDb, testMeasurement).toString() shouldEqual
      queryTester(s"SHOW FIELD KEYS ON $testDb FROM $testMeasurement")
  }
}

package com.github.fsanaulla.unit

import akka.http.scaladsl.model.Uri
import com.github.fsanaulla.query.RetentionPolicyManagementQuery
import com.github.fsanaulla.utils.InfluxDuration._
import com.github.fsanaulla.utils.TestHelper._
import com.github.fsanaulla.utils.{AkkaQueryHandler, TestCredentials}
import org.scalatest.{FlatSpecLike, Matchers}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.07.17
  */
class RetentionPolicyManagementQuerySpec
  extends RetentionPolicyManagementQuery[Uri]
    with AkkaQueryHandler
    with FlatSpecLike
    with Matchers
    with TestCredentials {

  val testRPName = "testRP"
  val testDBName = "testDB"

  "create retention policy" should "return correct query" in {
    createRetentionPolicyQuery(testRPName, testDBName, 4 hours, 3, Some(4 hours), default = true) shouldEqual queryTesterAuth(s"CREATE RETENTION POLICY $testRPName ON $testDBName DURATION 4h REPLICATION 3 SHARD DURATION 4h DEFAULT")

    createRetentionPolicyQuery(testRPName, testDBName, 4 hours, 3, None)(emptyCredentials) shouldEqual queryTester(s"CREATE RETENTION POLICY $testRPName ON $testDBName DURATION 4h REPLICATION 3")

    createRetentionPolicyQuery(testRPName, testDBName, 4 hours, 3, None, default = true) shouldEqual queryTesterAuth(s"CREATE RETENTION POLICY $testRPName ON $testDBName DURATION 4h REPLICATION 3 DEFAULT")

    createRetentionPolicyQuery(testRPName, testDBName, 4 hours, 3, Some(4 hours)) shouldEqual queryTesterAuth(s"CREATE RETENTION POLICY $testRPName ON $testDBName DURATION 4h REPLICATION 3 SHARD DURATION 4h")

  }

  "drop retention policy" should "return correct query" in {
    dropRetentionPolicyQuery(testRPName, testDBName) shouldEqual queryTesterAuth(s"DROP RETENTION POLICY $testRPName ON $testDBName")

    dropRetentionPolicyQuery(testRPName, testDBName)(emptyCredentials) shouldEqual queryTester(s"DROP RETENTION POLICY $testRPName ON $testDBName")
  }

  "update retention policy" should "return correct query" in {
    updateRetentionPolicyQuery(testRPName, testDBName, Some(4 hours), Some(3), Some(4 hours), default = true) shouldEqual queryTesterAuth(s"ALTER RETENTION POLICY $testRPName ON $testDBName DURATION 4h REPLICATION 3 SHARD DURATION 4h DEFAULT")

    updateRetentionPolicyQuery(testRPName, testDBName, Some(4 hours), None, None)(emptyCredentials) shouldEqual queryTester(s"ALTER RETENTION POLICY $testRPName ON $testDBName DURATION 4h")

    updateRetentionPolicyQuery(testRPName, testDBName, Some(4 hours), Some(3), None) shouldEqual queryTesterAuth(s"ALTER RETENTION POLICY $testRPName ON $testDBName DURATION 4h REPLICATION 3")

    updateRetentionPolicyQuery(testRPName, testDBName, None, Some(3), Some(4 hours))(emptyCredentials) shouldEqual queryTester(s"ALTER RETENTION POLICY $testRPName ON $testDBName REPLICATION 3 SHARD DURATION 4h")
  }
}

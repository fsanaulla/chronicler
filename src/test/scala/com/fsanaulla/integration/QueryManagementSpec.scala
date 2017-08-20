package com.fsanaulla.integration

import com.fsanaulla.InfluxClient
import com.fsanaulla.model.QueryInfo
import com.fsanaulla.utils.TestHelper._
import com.fsanaulla.utils.TestSpec

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 20.08.17
  */
class QueryManagementSpec extends TestSpec {

  val testDb = "query_db"

  "Query management operation" should "correctly work" in {
    // INIT INFLUX CLIENT
    val influx = InfluxClient(host = influxHost, username = credentials.username, password = credentials.password)

    influx.createDatabase(testDb).futureValue shouldEqual OkResult

    val queryInfo = influx.showQueries().futureValue.queryResult.find(_.query == s"CREATE DATABASE $testDb").value

    queryInfo shouldBe a [QueryInfo]

    influx.killQuery(queryInfo.queryId).futureValue shouldEqual OkResult

    influx.showQueries().futureValue.queryResult.find(_.query == s"CREATE DATABASE $testDb") shouldEqual None

    influx.dropDatabase(testDb).futureValue shouldEqual OkResult

    influx.close()

  }
}

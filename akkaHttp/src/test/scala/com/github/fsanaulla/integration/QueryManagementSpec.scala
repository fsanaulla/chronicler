package com.github.fsanaulla.integration

import com.github.fsanaulla.utils.TestSpec

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 20.08.17
  */
class QueryManagementSpec extends TestSpec {

//  val testDb = "query_db"
//
//  "Query management operation" should "correctly work" ignore {
//    // INIT INFLUX CLIENT
//    val influx = InfluxClient(host = influxHost, username = credentials.username, password = credentials.password)
//
//    influx.createDatabase(testDb).futureValue shouldEqual OkResult
//
//    val queryInfo = influx.showQueries().futureValue.queryResult.find(_.query == "SHOW QUERIES").value
//
//    queryInfo shouldBe a [QueryInfo]
//
//    influx.killQuery(queryInfo.queryId).futureValue shouldEqual OkResult
//
//    influx.showQueries().futureValue.queryResult.find(_.queryId == queryInfo.queryId) shouldEqual None
//
//    influx.dropDatabase(testDb).futureValue shouldEqual OkResult
//
//    influx.close()
//
//  }
}

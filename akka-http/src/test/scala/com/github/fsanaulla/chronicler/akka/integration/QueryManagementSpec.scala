package com.github.fsanaulla.chronicler.akka.integration

import com.github.fsanaulla.chronicler.akka.{InfluxAkkaHttpClient, InfluxDB}
import com.github.fsanaulla.core.model.QueryInfo
import com.github.fsanaulla.core.test.utils.ResultMatchers._
import com.github.fsanaulla.core.test.utils.TestSpec
import com.github.fsanaulla.scalatest.EmbeddedInfluxDB
import org.scalatest.Ignore

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 20.08.17
  */
@Ignore
class QueryManagementSpec extends TestSpec with EmbeddedInfluxDB {

  val testDb = "query_db"
  val cqName = "cq_name"
  val cqQuery = "SELECT * INTO meas1 FROM meas GROUP BY ad"

  lazy val influx: InfluxAkkaHttpClient = InfluxDB.connect()

  var queryId = 0

  "Query management operation" should "show queries" in {
    influx.createDatabase(testDb).futureValue shouldEqual OkResult

    influx.createCQ(testDb, cqName, cqQuery).futureValue shouldEqual OkResult

    val showQueris = influx.showQueries().futureValue.queryResult

    val query = showQueris.find(_.query == cqQuery).value

    query shouldBe a[QueryInfo]

    queryId = query.queryId

  }

  it should "kill queries" in {
    influx.killQuery(queryId).futureValue shouldEqual OkResult

    influx.showQueries().futureValue.queryResult.find(_.queryId == queryId) shouldEqual None

    influx.close() shouldEqual {}
  }
}

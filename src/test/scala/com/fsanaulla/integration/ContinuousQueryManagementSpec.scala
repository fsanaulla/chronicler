package com.fsanaulla.integration

import com.fsanaulla.InfluxClient
import com.fsanaulla.model.{ContinuousQuery, DatabaseInfo}
import com.fsanaulla.utils.Extension._
import com.fsanaulla.utils.TestHelper._

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 09.08.17 on Anna's birthday
  */
class ContinuousQueryManagementSpec extends IntegrationSpec {

  val testDB = "cq_db"
  val testCQ = "test_cq"
  val query = "SELECT mean(value) AS mean_value INTO mydb.autogen.aggregate FROM mydb.autogen.cpu_load_short GROUP BY time(30m)"
  val updateQuery = "SELECT mean(value) AS mean_value INTO mydb.autogen.new_aggr FROM mydb.autogen.cpu_load_short GROUP BY time(30m)"

  "CQ management operation" should "work correctly" in {

    // INIT INFLUX CLIENT
    val influx = InfluxClient(host)

    influx.createDatabase(testDB).sync shouldEqual OkResult

    influx.showDatabases().sync.queryResult.contains(DatabaseInfo(testDB)) shouldEqual true

    influx.createCQ(testDB, testCQ, query).sync shouldEqual OkResult

    influx.showCQ(testDB).sync.queryResult shouldEqual Seq(ContinuousQuery("test_cq", "CREATE CONTINUOUS QUERY test_cq ON cq_db BEGIN SELECT mean(value) AS mean_value INTO mydb.autogen.aggregate FROM mydb.autogen.cpu_load_short GROUP BY time(30m) END"))

    influx.updateCQ(testDB, testCQ, updateQuery).sync shouldEqual OkResult

    influx.showCQ(testDB).sync.queryResult.contains(ContinuousQuery("test_cq", "CREATE CONTINUOUS QUERY test_cq ON cq_db BEGIN SELECT mean(value) AS mean_value INTO mydb.autogen.new_aggr FROM mydb.autogen.cpu_load_short GROUP BY time(30m) END)")) shouldEqual true

    influx.dropCQ(testDB, testCQ).sync shouldEqual OkResult

    influx.showCQ(testDB).sync.queryResult shouldEqual Nil

    influx.dropDatabase(testDB).sync shouldEqual OkResult

    influx.close()
  }

}

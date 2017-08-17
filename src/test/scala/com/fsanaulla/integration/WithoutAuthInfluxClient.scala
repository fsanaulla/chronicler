package com.fsanaulla.integration

import com.fsanaulla.InfluxClient
import com.fsanaulla.model.AuthorizationException
import com.fsanaulla.utils.TestSpec

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 17.08.17
  */
class WithoutAuthInfluxClient extends TestSpec {

  "With out auth" should "correctly work" in {
    val influx = InfluxClient(influxHost)

    influx.createUser("some_name", "pass").futureValue.ex.get shouldBe a [AuthorizationException]

    influx.use("db").readJs("SELECT * FROM meas").futureValue.ex.get shouldBe a [AuthorizationException]

    influx.close()
  }
}

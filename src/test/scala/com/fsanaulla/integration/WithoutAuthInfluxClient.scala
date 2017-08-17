package com.fsanaulla.integration

import com.fsanaulla.InfluxClient
import com.fsanaulla.model.AuthorizationException
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Second, Seconds, Span}
import org.scalatest.{FlatSpec, Matchers, OptionValues}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 17.08.17
  */
class WithoutAuthInfluxClient extends FlatSpec with Matchers with ScalaFutures with OptionValues {

  implicit val pc = PatienceConfig(Span(20, Seconds), Span(1, Second))

  "With out auth" should "correctly work" in {
    val influx = InfluxClient("localhost")

    influx.createUser("some_name", "pass").futureValue.ex.value shouldBe a [AuthorizationException]

    influx.use("db").readJs("SELECT * FROM meas").futureValue.ex.value shouldBe a [AuthorizationException]

    influx.close()
  }
}

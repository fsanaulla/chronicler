package com.github.fsanaulla.core.test

import com.github.fsanaulla.core.utils.InfluxDuration._
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 13.08.17
  */
class InfluxDurationSpec extends FlatSpec with Matchers {

  "Influx duration with Int" should "correctly work" in {
   1.weeks + 2.days + 3.hours + 45.minutes + 55.seconds shouldEqual "1w2d3h45m55s"
  }

  "Influx duration with Long" should "correctly work" in {
    1L.weeks + 2L.days + 3L.hours + 45L.minutes + 55L.seconds + 123123123123L.nanoseconds shouldEqual "1w2d3h45m55s123123123123ns"
  }
}

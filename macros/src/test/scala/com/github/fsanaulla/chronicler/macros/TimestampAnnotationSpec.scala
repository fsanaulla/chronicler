/*
 * Copyright 2017-2018 Faiaz Sanaulla
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.fsanaulla.chronicler.macros

import com.github.fsanaulla.chronicler.core.model.InfluxFormatter
import com.github.fsanaulla.chronicler.macros.annotations._
import jawn.ast
import jawn.ast.{JArray, JNull, JNum, JString}
import org.scalatest.{FlatSpec, Matchers}

class TimestampAnnotationSpec extends FlatSpec with Matchers {

  "Macros" should "read generic timestamp from UTC/Epoch time representation" in {
    case class GeneralTimestamp(@tag name: String,
                                @tag surname: Option[String],
                                @field age: Int,
                                @timestamp time: Long)
    val fmt = Influx.formatter[GeneralTimestamp]

    fmt.read(JArray(Array(JString("2015-08-04T19:05:14.318570484Z"), JNum(4), JString("Fz"), JNull))) shouldEqual
      GeneralTimestamp("Fz", None, 4, 1438715114318570484L)

    fmt.read(JArray(Array(ast.LongNum(1438715114318570484L), JNum(4), JString("Fz"), JNull))) shouldEqual
      GeneralTimestamp("Fz", None, 4, 1438715114318570484L)
  }

  it should "read Epoch time representation" in {
    case class EpochTimestamp(@tag name: String,
                              @tag surname: Option[String],
                              @field age: Int,
                              @timestampEpoch time: Long)
    val fmt = Influx.formatter[EpochTimestamp]

    fmt.read(JArray(Array(ast.LongNum(1438715114318570484L), JNum(4), JString("Fz"), JNull))) shouldEqual
      EpochTimestamp("Fz", None, 4, 1438715114318570484L)
  }

  it should "read UTC time representation" in {
    case class UTCTimestamp(@tag name: String,
                            @tag surname: Option[String],
                            @field age: Int,
                            @timestampUTC time: Long)
    val fmt: InfluxFormatter[UTCTimestamp] = Influx.formatter[UTCTimestamp]

    fmt.read(JArray(Array(JString("2015-08-04T19:05:14.318570484Z"), JNum(4), JString("Fz"), JNull))) shouldEqual
      UTCTimestamp("Fz", None, 4, 1438715114318570484L)
  }
}

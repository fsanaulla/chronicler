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

import com.github.fsanaulla.chronicler.core.model.InfluxReader
import com.github.fsanaulla.chronicler.macros.annotations.{field, tag, timestamp}
import jawn.ast._
import org.scalatest.{FlatSpec, Matchers}

class MacroReaderSpec extends FlatSpec with Matchers {

  case class Test(@tag name: String,
                  @tag surname: Option[String],
                  @field age: Int)

  case class Test1(@tag name: String,
                   @tag surname: Option[String],
                   @field age: Int,
                   @timestamp time: Long)

  val rd: InfluxReader[Test] = Macros.reader[Test]
  val rd1: InfluxReader[Test1] = Macros.reader[Test1]

  "Macros.reader" should "read with None and ignore time" in {
    rd.read(JArray(Array(JString("2015-08-04T19:05:14.318570484Z"), JNum(4), JString("Fz"), JNull))) shouldEqual Test("Fz", None, 4)
  }

  it should "read with Some and ignore time" in {
    rd.read(JArray(Array(JString("2015-08-04T19:05:14Z"), JNum(4), JString("Fz"), JString("Sr")))) shouldEqual Test("Fz", Some("Sr"), 4)
  }

  it should "read with timestamp" in {
    rd1.read(JArray(Array(JString("2015-08-04T19:05:14.318570484Z"), JNum(4), JString("Fz"), JNull))) shouldEqual Test1("Fz", None, 4, 1438715114318570484L)
  }
}

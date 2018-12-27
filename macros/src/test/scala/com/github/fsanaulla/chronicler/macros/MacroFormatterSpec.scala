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
import com.github.fsanaulla.chronicler.macros.annotations.{field, tag, timestamp}
import jawn.ast._
import org.scalatest.{FlatSpec, Matchers}

class MacroFormatterSpec extends FlatSpec with Matchers {

  case class Test(@tag name: String,
                  @tag surname: Option[String],
                  @field age: Int,
                  @timestamp time: Long)

  val fm: InfluxFormatter[Test] = Influx.formatter[Test]

  it should "read with None" in {
    fm
      .read(JArray(Array(JString("2015-08-04T19:05:14.318570484Z"), JNum(4), JString("Fz"), JNull)))
      .shouldEqual(Test("Fz", None, 4, 1438715114318570484L))
  }

  it should "read with Some" in {
    fm
      .read(JArray(Array(JString("2015-08-04T19:05:14.318570484Z"), JNum(4), JString("Fz"), JString("Sz"))))
      .shouldEqual(Test("Fz", Some("Sz"), 4, 1438715114318570484L))
  }

  it should "write with None" in {
    fm
      .write(Test("tName", None, 65, 1438715114318570484L))
      .shouldEqual("name=tName age=65i 1438715114318570484")
  }

  it should "write with Some" in {
    fm
      .write(Test("tName", Some("Sz"), 65, 1438715114318570484L))
      .shouldEqual("name=tName,surname=Sz age=65i 1438715114318570484")
  }
}

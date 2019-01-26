/*
 * Copyright 2017-2019 Faiaz Sanaulla
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

import com.github.fsanaulla.chronicler.core.model.InfluxWriter
import com.github.fsanaulla.chronicler.macros.annotations.{field, tag, timestamp}
import org.scalatest.{FlatSpec, Matchers}

class MacroWriterSpec extends FlatSpec with Matchers {

  case class Test(@tag name: String,
                  @tag surname: Option[String],
                  @field school: String,
                  @field age: Int)

  case class Test1(@tag name: String,
                   @tag surname: Option[String],
                   @field age: Int,
                   @field school: String,
                   @timestamp time: Long)

  val wr: InfluxWriter[Test] = Influx.writer[Test]
  val wr1: InfluxWriter[Test1] = Influx.writer[Test1]

  "Macros.writer" should "write with None" in {
    wr.write(Test("nm", None, "Berkly", 65)) shouldEqual "name=nm school=\"Berkly\",age=65i"
  }

  it should "write with Some" in {
    wr.write(Test("nm", Some("sn"), "Berkly", 65)) shouldEqual "name=nm,surname=sn school=\"Berkly\",age=65i"
  }

  it should "write with timestamp" in {
    wr1.write(Test1("nm", Some("sn"), 65, "Berkly", 1438715114318570484L)) shouldEqual "name=nm,surname=sn age=65i,school=\"Berkly\" 1438715114318570484"
  }
}

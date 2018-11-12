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

import com.github.fsanaulla.chronicler.core.model.InfluxWriter
import com.github.fsanaulla.chronicler.macros.annotations.{field, tag}
import org.scalatest.{FlatSpec, Matchers}

class MacroEscapeSpec extends FlatSpec with Matchers {
  "Macros" should "escape spaces" in {
    case class Test(@tag `name `: String, @field `age `: Int)
    implicit val wr: InfluxWriter[Test] = Influx.writer[Test]
    val t = Test("My Name", 5)
    wr.write(t) shouldEqual "name\\ =My\\ Name age\\ =5i"
  }

  it should "escape commas" in {
    case class Test(@tag `name,`: String, @field `age,`: Int)
    implicit val wr: InfluxWriter[Test] = Influx.writer[Test]
    val t = Test("My,Name", 5)
    wr.write(t) shouldEqual "name\\,=My\\,Name age\\,=5i"
  }

  it should "escape equals sign" in {
    case class Test(@tag `name=`: String, @field `age=`: Int)
    implicit val wr: InfluxWriter[Test] = Influx.writer[Test]
    val t = Test("My=Name", 5)
    wr.write(t) shouldEqual "name\\==My\\=Name age\\==5i"
  }

  it should "escape complex case" in {
    case class Test(@tag `name, =`: String, @field `age= ,`: Int)
    implicit val wr: InfluxWriter[Test] = Influx.writer[Test]
    val t = Test("My= Name", 5)
    wr.write(t) shouldEqual "name\\,\\ \\==My\\=\\ Name age\\=\\ \\,=5i"
  }
}

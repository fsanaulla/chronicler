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

import java.time.Instant

import com.github.fsanaulla.chronicler.macros.annotations.{field, tag, timestamp}
import org.scalatest.{FlatSpec, Matchers}

class TestSpec extends FlatSpec with Matchers {

  it should "work" in {

    val time = Instant.now().toEpochMilli
    case class Test(@tag name: String,
                    @tag surname: String,
                    @tag school: Option[String],
                    @field city: String,
                    @field age: Int,
                    @field male: Boolean,
                    @timestamp time: Long = time)
    val wr = Influx.writerNew[Test]

    wr
      .write(Test("f", "s", None, "X", 24, male = true)) shouldEqual "name=f,surname=s city=\"X\",age=24i,male=true" + " " + time
    wr
      .write(Test("f", "s", Some("a"), "Y", 49, male = false)) shouldEqual "name=f,surname=s,school=a city=\"Y\",age=49i,male=false" + " " + time
  }

}

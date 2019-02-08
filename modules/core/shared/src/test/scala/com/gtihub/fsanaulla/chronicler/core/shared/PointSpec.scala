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

package com.gtihub.fsanaulla.chronicler.core.shared

import com.github.fsanaulla.chronicler.core.model._
import org.scalatest.{FlatSpec, Matchers}

class PointSpec extends FlatSpec with Matchers {
  "Point" should "escape spaces" in {
    val p = Point("test meas")
      .addTag("tag key", "tag value")
      .addField("field space", 1)

    p.serialize shouldEqual "test\\ meas,tag\\ key=tag\\ value field\\ space=1i"
  }

  it should "escape commas" in {
    val p = Point("test,meas")
      .addTag("tag,key", "tag,value")
      .addField("field,space", 1)

    p.serialize shouldEqual "test\\,meas,tag\\,key=tag\\,value field\\,space=1i"
  }

  it should "escape equals sign" in {
    val p = Point("test=meas")
      .addTag("tag=key", "tag=value")
      .addField("field=space", 1)

    p.serialize shouldEqual "test=meas,tag\\=key=tag\\=value field\\=space=1i"
  }

  it should "escape complex case" in {
    val p = Point("test, meas")
      .addTag("tag, =key", "tag= ,value")
      .addField("field=, space", 1)

    p.serialize shouldEqual "test\\,\\ meas,tag\\,\\ \\=key=tag\\=\\ \\,value field\\=\\,\\ space=1i"
  }
}

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

package com.github.fsanaulla.chronicler.urlhttp.shared

import com.softwaremill.sttp.{ResponseAs, asString}
import jawn.ast.{JNull, JParser, JValue}

import scala.util.Success

package object formats {
  val asJson: ResponseAs[JValue, Nothing] =
    asString
      .map(JParser.parseFromString)
      .map {
        case Success(jv) => jv
        case _ => JNull
      }
}

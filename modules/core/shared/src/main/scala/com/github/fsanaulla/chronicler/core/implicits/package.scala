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

package com.github.fsanaulla.chronicler.core

import com.github.fsanaulla.chronicler.core.alias.{ErrorOr, Id}
import com.github.fsanaulla.chronicler.core.enums.{Destinations, Privileges}
import com.github.fsanaulla.chronicler.core.jawn._
import com.github.fsanaulla.chronicler.core.model._
import org.typelevel.jawn.ast.{JArray, JValue}

package object implicits {

  private[this] def exception(msg: String) = new ParsingException(msg)

  implicit final class RichString(private val str: String) extends AnyVal {

    def escapeKey: String =
      regex.tagPattern.matcher(str).replaceAll("\\\\$1")

    def escapeMeas: String =
      regex.measPattern.matcher(str).replaceAll("\\\\$1")
  }

  implicit object StringInfluxReader extends InfluxReader[String] {
    override def read(js: JArray): ErrorOr[String] = js.vs match {
      case Array(str: JValue) =>
        Right(str)
      case _ =>
        Left(exception(s"Can't deserialize $js to String"))
    }

    override def readUnsafe(js: JArray): String = js.vs match {
      case Array(str: JValue) => str
      case _ =>
        throw exception(s"Can't deserialize $js to String")
    }
  }
}

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

package com.github.fsanaulla.chronicler.core.model

import java.io.{Serializable => JSerializable}

import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import org.typelevel.jawn.ast.JArray

import scala.annotation.implicitNotFound

/** When trying deserialize JSON from influx, don't forget that influx sort field in db alphabetically.
  */
@implicitNotFound(
  "No InfluxReader found for type ${T}. Try to implement an implicit InfluxReader for this type."
)
trait InfluxReader[T] extends JSerializable {

  /** Read wrapping in Either[Throwable, T]
    *
    * @param js - jarray
    * @return   - Either[Throwable, T]
    */
  def read(js: JArray): ErrorOr[T]

  /** Read unsafe throwing exception
    *
    * @param js - jarray
    * @return   - T
    * @since - 0.5.2
    */
  def readUnsafe(js: JArray): T
}

object InfluxReader {
  def apply[T](implicit rd: InfluxReader[T]): InfluxReader[T] = rd
}

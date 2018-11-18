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

import com.github.fsanaulla.chronicler.core.model.{InfluxFormatter, InfluxReader, InfluxWriter}

import scala.language.experimental.macros

/***
  * Provide all necessary method for compile-time code generation
  */
object Influx {

  /** Generate [[InfluxWriter]] for type A */
  def writer[A]: InfluxWriter[A] = macro InfluxImpl.writer_impl[A]

  /** Generate [[InfluxReader]] for type A */
  def reader[A]: InfluxReader[A] = macro InfluxImpl.reader_impl[A]

  /** Generate [[InfluxFormatter]] for type A */
  def formatter[A]: InfluxFormatter[A] = macro InfluxImpl.format_impl[A]
}

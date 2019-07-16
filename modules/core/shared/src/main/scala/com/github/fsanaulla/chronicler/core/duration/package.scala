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

package object duration {

  implicit final class IntDurationExtension(private val i: Int) extends AnyVal {
    def nanoseconds: String  = i.toString + "ns"
    def microseconds: String = i.toString + "u"
    def milliseconds: String = i.toString + "ms"
    def seconds: String      = i.toString + "s"
    def minutes: String      = i.toString + "m"
    def hours: String        = i.toString + "h"
    def days: String         = i.toString + "d"
    def weeks: String        = i.toString + "w"
  }

  implicit final class LongDurationExtension(private val l: Long) extends AnyVal {
    def nanoseconds: String  = l.toString + "ns"
    def microseconds: String = l.toString + "u"
    def milliseconds: String = l.toString + "ms"
    def seconds: String      = l.toString + "s"
    def minutes: String      = l.toString + "m"
    def hours: String        = l.toString + "h"
    def days: String         = l.toString + "d"
    def weeks: String        = l.toString + "w"
  }

}

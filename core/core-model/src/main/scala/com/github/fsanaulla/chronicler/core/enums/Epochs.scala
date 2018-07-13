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

package com.github.fsanaulla.chronicler.core.enums

import enumeratum.EnumEntry

import scala.collection.immutable

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 29.07.17
  */
sealed abstract class Epoch extends EnumEntry {
  override def toString: String = entryName
}

object Epochs extends enumeratum.Enum[Epoch] {
  val values: immutable.IndexedSeq[Epoch] = findValues

  case object NANOSECONDS  extends Epoch { override val entryName: String = "ns" }
  case object MICROSECONDS extends Epoch { override val entryName: String = "u"  }
  case object MILLISECONDS extends Epoch { override val entryName: String = "ms" }
  case object SECONDS      extends Epoch { override val entryName: String = "s"  }
  case object MINUTES      extends Epoch { override val entryName: String = "m"  }
  case object HOURS        extends Epoch { override val entryName: String = "h"  }
}

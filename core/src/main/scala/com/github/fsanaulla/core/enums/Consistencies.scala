package com.github.fsanaulla.core.enums

import enumeratum.EnumEntry

import scala.collection.immutable

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 29.07.17
  */
sealed trait Consistency extends EnumEntry

object Consistencies extends enumeratum.Enum[Consistency]{

  val values: immutable.IndexedSeq[Consistency] = findValues

  case object ONE extends Consistency
  case object QUORUM extends Consistency
  case object ALL extends Consistency
  case object ANY extends Consistency
}

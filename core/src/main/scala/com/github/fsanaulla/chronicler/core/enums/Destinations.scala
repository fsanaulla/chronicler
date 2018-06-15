package com.github.fsanaulla.chronicler.core.enums

import enumeratum.EnumEntry

import scala.collection.immutable

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
sealed trait Destination extends EnumEntry

object Destinations extends enumeratum.Enum[Destination] {
  val values: immutable.IndexedSeq[Destination] = findValues

  case object ALL extends Destination
  case object ANY extends Destination
}

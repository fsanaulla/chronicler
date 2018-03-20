package com.github.fsanaulla.core.enums

import enumeratum.EnumEntry

import scala.collection.immutable

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 29.07.17
  */
sealed trait Epoch extends EnumEntry

object Epochs extends enumeratum.Enum[Epoch] {

  val values: immutable.IndexedSeq[Epoch] = findValues

  case object NANOSECONDS extends Epoch
  case object MICROSECONDS extends Epoch
  case object MILLISECONDS extends Epoch
  case object SECONDS extends Epoch
  case object MINUTES extends Epoch
  case object HOURS extends Epoch
}

package com.github.fsanaulla.core.enums

import enumeratum.EnumEntry

import scala.collection.immutable

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 29.07.17
  */
sealed trait Precision extends EnumEntry

object Precisions extends enumeratum.Enum[Precision] {

  val values: immutable.IndexedSeq[Precision] = findValues

  case object NANOSECONDS extends Precision
  case object MICROSECONDS extends Precision
  case object MILLISECONDS extends Precision
  case object SECONDS extends Precision
  case object MINUTES extends Precision
  case object HOURS extends Precision
}

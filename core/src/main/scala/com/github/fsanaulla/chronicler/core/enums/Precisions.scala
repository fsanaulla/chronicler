package com.github.fsanaulla.chronicler.core.enums

import enumeratum.EnumEntry

import scala.collection.immutable

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 29.07.17
  */
sealed abstract class Precision(override val entryName: String) extends EnumEntry {
  override def toString: String = entryName
}

object Precisions extends enumeratum.Enum[Precision] {
  val values: immutable.IndexedSeq[Precision] = findValues

  case object NANOSECONDS extends Precision("ns")
  case object MICROSECONDS extends Precision("u")
  case object MILLISECONDS extends Precision("ms")
  case object SECONDS extends Precision("s")
  case object MINUTES extends Precision("m")
  case object HOURS extends Precision("h")
}

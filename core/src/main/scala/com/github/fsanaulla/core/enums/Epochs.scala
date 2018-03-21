package com.github.fsanaulla.core.enums

import enumeratum.EnumEntry

import scala.collection.immutable

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 29.07.17
  */
sealed abstract class Epoch(override val entryName: String) extends EnumEntry {
  override def toString: String = entryName
}

object Epochs extends enumeratum.Enum[Epoch] {
  val values: immutable.IndexedSeq[Epoch] = findValues

  case object NANOSECONDS extends Epoch("ns")
  case object MICROSECONDS extends Epoch("u")
  case object MILLISECONDS extends Epoch("ms")
  case object SECONDS extends Epoch("s")
  case object MINUTES extends Epoch("m")
  case object HOURS extends Epoch("h")
}

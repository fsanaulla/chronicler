package com.github.fsanaulla.core.enums

import enumeratum.EnumEntry

import scala.collection.immutable

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 29.07.17
  */
sealed abstract class Consistency(override val entryName: String) extends EnumEntry {
  override def toString: String = entryName
}

object Consistencies extends enumeratum.Enum[Consistency]{
  val values: immutable.IndexedSeq[Consistency] = findValues

  case object ONE extends Consistency("one")
  case object QUORUM extends Consistency("quorum")
  case object ALL extends Consistency("all")
  case object ANY extends Consistency("any")
}

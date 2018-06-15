package com.github.fsanaulla.chronicler.core.enums

import enumeratum.EnumEntry

import scala.collection.immutable

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 29.07.17
  */
sealed trait Privilege extends EnumEntry

object Privileges extends enumeratum.Enum[Privilege] {
  val values: immutable.IndexedSeq[Privilege] = findValues

  case object READ extends Privilege
  case object WRITE extends Privilege
  case object ALL extends Privilege
  case object NO_PRIVILEGES extends Privilege {
    override def toString: String = "NO PRIVILEGES"
  }
}

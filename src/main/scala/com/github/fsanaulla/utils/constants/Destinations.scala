package com.github.fsanaulla.utils.constants

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
object Destinations {

  implicit def dest2Value(dest: Destination): String = dest.value

  sealed trait Destination {

    def value: String
  }

  case object ALL extends Destination {

    override def value: String = "ALL"
  }

  case object ANY extends Destination {

    override def value: String = "ANY"
  }
}

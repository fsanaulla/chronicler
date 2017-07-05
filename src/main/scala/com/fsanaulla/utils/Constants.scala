package com.fsanaulla.utils

import com.fsanaulla.utils.Constants.Consistency.Consistency
import com.fsanaulla.utils.Constants.Epoch.Epoch
import com.fsanaulla.utils.Constants.Precision.Precision

/**
  * Created by fayaz on 04.07.17.
  */
object Constants {

  implicit def epoch2Value(epoch: Epoch): String = epoch.toString
  implicit def cosistency2Value(consistency: Consistency): String = consistency.toString
  implicit def precision2Value(precision: Precision): String = precision.toString

  object Epoch extends Enumeration {
    type Epoch = Value
    val NANOS = Value("ns")
    val MICROS = Value("u")
    val MILLIS = Value("ms")
    val SECONDS = Value("s")
    val MINUTES = Value("m")
    val HOURS = Value("h")
  }

  object Consistency extends Enumeration {
    type Consistency = Value
    val ANY = Value("any")
    val ONE = Value("one")
    val QUORUM = Value("quorum")
    val ALL = Value("all")
  }

  object Precision extends Enumeration {
    type Precision = Value
    val NANOS = Value("ns")
    val MICROS = Value("u")
    val MILLIS = Value("ms")
    val SECONDS = Value("s")
    val MINUTES = Value("m")
    val HOURS = Value("h")
  }

}

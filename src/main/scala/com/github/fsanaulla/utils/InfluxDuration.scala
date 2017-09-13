package com.github.fsanaulla.utils

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 29.07.17
  */
object InfluxDuration {

  implicit class IntDurationExtension(i: Int) {

    def nanoseconds: String = i.toString + "ns"

    def microseconds: String = i.toString + "u"

    def milliseconds: String = i.toString + "ms"

    def seconds: String = i.toString + "s"

    def minutes: String = i.toString + "m"

    def hours: String = i.toString + "h"

    def days: String = i.toString + "d"

    def weeks: String = i.toString + "w"
  }

  implicit class LongDurationExtension(l: Long) {

    def nanoseconds: String = l.toString + "ns"

    def microseconds: String = l.toString + "u"

    def milliseconds: String = l.toString + "ms"

    def seconds: String = l.toString + "s"

    def minutes: String = l.toString + "m"

    def hours: String = l.toString + "h"

    def days: String = l.toString + "d"

    def weeks: String = l.toString + "w"
  }
}

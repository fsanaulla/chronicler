package com.gtihub.fsanaulla.chronicler.core.model

import com.github.fsanaulla.chronicler.core.utils.InfluxDuration._
import org.scalacheck.{Prop, Properties}

class InfluxDurationProperties extends Properties("InfluxDuration") {

  def toInfluxDuration[@specialized(Int, Long) T](w: T, d: T, h: T, m: T, s: T) =
    s"${w}w${d}d${h}h${m}m${s}s"

  property("Int builder") = Prop.forAll {
    (w: Int, d: Int, h: Int, m: Int, s: Int) =>

      val implVal = w.weeks + d.days + h.hours + m.minutes + s.seconds
      val expVal = toInfluxDuration(w, d, h, m, s)

    implVal == expVal
  }

  property("Long builder") = Prop.forAll {
    (w: Long, d: Long, h: Long, m: Long, s: Long) =>

      val implVal = w.weeks + d.days + h.hours + m.minutes + s.seconds
      val expVal = toInfluxDuration(w, d, h, m, s)

      implVal == expVal
  }

}

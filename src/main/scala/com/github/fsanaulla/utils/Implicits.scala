package com.github.fsanaulla.utils

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 18.09.17
  */
object Implicits {

  implicit def bd2Int(bd: BigDecimal): Int = bd.toInt

  implicit def bd2Long(bd: BigDecimal): Long = bd.toLong

  implicit def bd2Double(bd: BigDecimal): Double = bd.toDouble
}

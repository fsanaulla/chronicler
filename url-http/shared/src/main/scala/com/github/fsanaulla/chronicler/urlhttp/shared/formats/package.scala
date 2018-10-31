package com.github.fsanaulla.chronicler.urlhttp.shared

import com.softwaremill.sttp.{ResponseAs, asString}
import jawn.ast.{JNull, JParser, JValue}

import scala.util.Success

package object formats {
  val asJson: ResponseAs[JValue, Nothing] =
    asString
      .map(JParser.parseFromString)
      .map {
        case Success(jv) => jv
        case _ => JNull
      }
}

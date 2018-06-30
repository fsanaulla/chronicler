package com.github.fsanaulla.chronicler.urlhttp.utils

import com.softwaremill.sttp.{ResponseAs, asString}
import jawn.ast.{JNull, JParser, JValue}

import scala.util.Success

private[urlhttp] object ResponseFormats {

  val asJson: ResponseAs[JValue, Nothing] = {
    asString
      .map(JParser.parseFromString)
      .map {
        case Success(jv) => jv
        case _           => JNull
      }
  }
}

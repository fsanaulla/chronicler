package com.github.fsanaulla.chronicler.urlhttp.utils

import com.softwaremill.sttp.{Id, RequestT}
import jawn.ast.JValue

private[urlhttp] object Aliases {
  type Request = RequestT[Id, JValue, Nothing]
}

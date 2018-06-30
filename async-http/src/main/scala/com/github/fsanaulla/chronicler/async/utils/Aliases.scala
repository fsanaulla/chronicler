package com.github.fsanaulla.chronicler.async.utils

import com.softwaremill.sttp.{Id, RequestT}
import jawn.ast.JValue

private[async] object Aliases {
  type Request = RequestT[Id, JValue, Nothing]
}

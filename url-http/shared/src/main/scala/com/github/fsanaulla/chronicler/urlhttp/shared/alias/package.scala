package com.github.fsanaulla.chronicler.urlhttp.shared

import com.softwaremill.sttp.{Id, RequestT}
import jawn.ast.JValue

package object alias {
  type Request = RequestT[Id, JValue, Nothing]
}

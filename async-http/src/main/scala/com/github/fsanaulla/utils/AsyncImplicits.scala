package com.github.fsanaulla.utils

import com.softwaremill.sttp.StringBody

private[fsanaulla] object AsyncImplicits {

  implicit def str2strbody(str: String): StringBody = StringBody(str, "UTF-8")
}

package com.github.fsanaulla.chronicler.ahc.shared

package object handlers {
  // extract encoding from content type
  def encodingFromContentType(ct: String): Option[String] =
    ct.split(";")
      .map(_.trim.toLowerCase)
      .collectFirst {
        case s if s.startsWith("charset=") && s.substring(8).trim != "" => s.substring(8).trim
      }
}

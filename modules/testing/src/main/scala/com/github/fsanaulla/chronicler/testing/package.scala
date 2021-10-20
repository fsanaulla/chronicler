package com.github.fsanaulla.chronicler

import scala.io.Source

package object testing {
  def getJsonStringFromFile(name: String): String =
    Source.fromFile(getClass.getResource(name).toURI).mkString
}

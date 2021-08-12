package com.github.fsanaulla.chronicler.urlhttp.shared

import sttp.client3.Response

import scala.io.Source

package object handlers {
  def mkResponse(str: String): Response[Either[String, String]] =
    Response.ok(Right(str))

  def getJsonStringFromFile(name: String): String =
    Source.fromFile(getClass.getResource(name).toURI).mkString
}

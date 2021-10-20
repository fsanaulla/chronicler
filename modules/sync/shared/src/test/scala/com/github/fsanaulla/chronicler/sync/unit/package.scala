package com.github.fsanaulla.chronicler.sync

import sttp.client3.Response

import scala.io.Source

package object unit {
  def mkResponse(str: String): Response[Either[String, String]] =
    Response.ok(Right(str))
}

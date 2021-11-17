package com.github.fsanaulla.chronicler.core.components

import com.github.fsanaulla.chronicler.core.auth.InfluxCredentials

trait RequestBuilder[Req, U, B] {
  def credentials: Option[InfluxCredentials]
  def get(uri: U, compress: Boolean): Req
  def post(uri: U, body: B, compress: Boolean): Req
  def post(uri: U): Req
}

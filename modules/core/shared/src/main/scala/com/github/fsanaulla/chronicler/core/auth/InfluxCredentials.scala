package com.github.fsanaulla.chronicler.core.auth

trait InfluxCredentials

object InfluxCredentials {
  final case class Basic(username: String, password: String) extends InfluxCredentials
  final case class Jwt(token: String)                        extends InfluxCredentials
}

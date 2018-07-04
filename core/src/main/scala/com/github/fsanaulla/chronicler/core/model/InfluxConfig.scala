package com.github.fsanaulla.chronicler.core.model

/** Configuration file for simplify cooperation with influx */
final case class InfluxConfig(host: String,
                              port: Int,
                              credentials: Option[InfluxCredentials] = None)

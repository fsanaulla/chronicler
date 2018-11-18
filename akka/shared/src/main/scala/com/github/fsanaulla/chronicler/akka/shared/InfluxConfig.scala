package com.github.fsanaulla.chronicler.akka.shared

import akka.http.scaladsl.HttpsConnectionContext
import com.github.fsanaulla.chronicler.core.model.InfluxCredentials

final case class InfluxConfig(host: String,
                              port: Int = 8086,
                              credentials: Option[InfluxCredentials] = None,
                              gzipped: Boolean = false,
                              httpsContext: Option[HttpsConnectionContext] = None)

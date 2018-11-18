package com.github.fsanaulla.chronicler.urlhttp.shared

import com.github.fsanaulla.chronicler.core.model.InfluxCredentials
import com.github.fsanaulla.chronicler.urlhttp.shared.InfluxUrlClient.CustomizationF

final case class InfluxConfig(host: String,
                              port: Int = 8086,
                              credentials: Option[InfluxCredentials] = None,
                              gzipped: Boolean = false,
                              customization: Option[CustomizationF] = None)

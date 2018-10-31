package com.github.fsanaulla.chronicler.urlhttp.io.models

import com.github.fsanaulla.chronicler.core.model.{GzippedHttpConfig, InfluxCredentials}
import com.github.fsanaulla.chronicler.urlhttp.shared.UrlHttpClient.CustomizationF

final case class InfluxConfig(host: String,
                              port: Int,
                              credentials: Option[InfluxCredentials],
                              gzipped: Boolean,
                              customization: Option[CustomizationF]) extends GzippedHttpConfig

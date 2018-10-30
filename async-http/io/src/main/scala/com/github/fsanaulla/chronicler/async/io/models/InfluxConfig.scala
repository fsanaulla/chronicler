package com.github.fsanaulla.chronicler.async.io.models

import com.github.fsanaulla.chronicler.core.model.{GzippedHttpConfig, InfluxCredentials}
import org.asynchttpclient.AsyncHttpClientConfig

final case class InfluxConfig(host: String,
                              port: Int,
                              credentials: Option[InfluxCredentials],
                              gzipped: Boolean,
                              asyncClientConfig: Option[AsyncHttpClientConfig]) extends GzippedHttpConfig

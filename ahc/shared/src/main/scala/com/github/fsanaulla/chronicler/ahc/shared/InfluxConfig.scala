package com.github.fsanaulla.chronicler.ahc.shared

import com.github.fsanaulla.chronicler.core.model.InfluxCredentials
import org.asynchttpclient.AsyncHttpClientConfig

final case class InfluxConfig(host: String,
                              port: Int = 8086,
                              credentials: Option[InfluxCredentials] = None,
                              gzipped: Boolean = false,
                              asyncClientConfig: Option[AsyncHttpClientConfig] = None)

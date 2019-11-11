/*
 * Copyright 2017-2019 Faiaz Sanaulla
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.fsanaulla.chronicler.ahc.shared

import org.asynchttpclient.{
  AsyncHttpClientConfig,
  DefaultAsyncHttpClient,
  DefaultAsyncHttpClientConfig
}

class InfluxAhcClient(asyncClientConfig: Option[AsyncHttpClientConfig]) { self: AutoCloseable =>
  private[this] val config = {
    val default = {
      val b = new DefaultAsyncHttpClientConfig.Builder()
      b.setCompressionEnforced(false)
      b.build()
    }

    asyncClientConfig.getOrElse(default)
  }

  private[ahc] val schema: String =
    if (config.isUseOpenSsl) "https" else "http"

  private[ahc] implicit val client: DefaultAsyncHttpClient =
    new DefaultAsyncHttpClient(config)

  override def close(): Unit = client.close()
}

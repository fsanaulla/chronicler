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

package com.github.fsanaulla.chronicler.urlhttp.shared.handlers

import com.github.fsanaulla.chronicler.core.components.RequestExecutor
import com.github.fsanaulla.chronicler.urlhttp.shared.Url
import requests._

import scala.util.Try

private[urlhttp] final class UrlRequestExecutor(ssl: Boolean) extends RequestExecutor[Try, Response, Url, String] {
  /**
    * Execute uri
    *
    * @param uri - request uri
    * @return    - Return wrapper response
    */
  override def executeUri(uri: Url): Try[Response] = {
    Try {
      requests.get(
        uri.mkUrl,
        params = uri.params,
        verifySslCerts = ssl
      )
    }
  }

  override def execute(uri: Url, body: String, gzipped: Boolean): Try[Response] = {
    Try {
      requests.post(
        uri.mkUrl,
        RequestAuth.Empty,
        uri.params,
        verifySslCerts = ssl,
        data = RequestBlob.StringRequestBlob(body)
      )
    }
  }
}

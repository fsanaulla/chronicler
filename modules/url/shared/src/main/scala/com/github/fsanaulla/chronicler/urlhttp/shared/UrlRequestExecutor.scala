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

package com.github.fsanaulla.chronicler.urlhttp.shared

import com.github.fsanaulla.chronicler.core.components.RequestExecutor
import com.github.fsanaulla.chronicler.core.gzip
import com.github.fsanaulla.chronicler.urlhttp.shared.UrlRequestExecutor.contentEncoding
import sttp.client3._
import sttp.model._

import java.nio.charset.StandardCharsets
import scala.util.Try

private[urlhttp] final class UrlRequestExecutor(backend: SttpBackend[Try, Any])
    extends RequestExecutor[Try, ResponseE, Uri, String] {

  /**
    * Execute HTTP GET request
    *
    * @param uri - request uri
    * @return    - Return wrapper response
    */
  override def get(uri: Uri, compression: Boolean): Try[ResponseE] = {
    val maybeGzipped =
      // contains accept encoding header
      if (compression) basicRequest.get(uri)
      else emptyRequest.get(uri)

    val response = backend.send(maybeGzipped.response(asString))

    response
  }

  override def post(uri: Uri): Try[ResponseE] =
    backend.send(emptyRequest.post(uri))

  override def post(
      uri: Uri,
      body: String,
      compression: Boolean
  ): Try[ResponseE] = {
    val bts         = body.getBytes(StandardCharsets.UTF_8)
    val requestBase = emptyRequest.post(uri)

    val maybeGzipped = if (compression) {
      // gzip body
      val (length, entity) = gzip.compress(bts)

      requestBase.headers(contentEncoding).contentLength(length.toLong).body(entity)
    } else requestBase.body(bts)

    backend.send(maybeGzipped)
  }
}

object UrlRequestExecutor {
  val contentEncoding: Header = Header.unsafeApply("Content-Encoding", "gzip")
}

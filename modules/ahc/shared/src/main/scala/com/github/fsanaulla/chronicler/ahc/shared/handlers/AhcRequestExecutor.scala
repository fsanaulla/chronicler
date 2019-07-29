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

package com.github.fsanaulla.chronicler.ahc.shared.handlers

import com.github.fsanaulla.chronicler.ahc.shared.formats._
import com.github.fsanaulla.chronicler.core.components.RequestExecutor
import com.github.fsanaulla.chronicler.core.gzip
import com.softwaremill.sttp.{sttp, HeaderNames, MediaTypes, Response, SttpBackend, Uri}
import org.typelevel.jawn.ast.JValue

import scala.concurrent.Future

private[ahc] final class AhcRequestExecutor(implicit backend: SttpBackend[Future, Nothing])
  extends RequestExecutor[Future, Response[JValue], Uri, String] {

  /**
    * Execute uri
    *
    * @param uri - request uri
    * @return    - Return wrapper response
    */
  override def get(uri: Uri, gzipped: Boolean = false): Future[Response[JValue]] = {
    val request = sttp
      .get(uri)
      .response(asJson)

    val maybeEncoded =
      if (gzipped) request.header(HeaderNames.AcceptEncoding, "gzip", replaceExisting = true)
      else request

    maybeEncoded.send()
  }

  override def post(
      uri: Uri,
      body: String,
      gzipped: Boolean
    ): Future[Response[JValue]] = {
    val req = sttp.post(uri).response(asJson)
    val maybeEncoded = if (gzipped) {
      val (length, data) = gzip.compress(body.getBytes())
      req
      // it fails with input stream, using byte array instead
        .body(data)
        .header(HeaderNames.ContentEncoding, "gzip", replaceExisting = true)
        .contentType(MediaTypes.Binary)
        .contentLength(length)
    } else req.body(body)

    maybeEncoded.send()
  }
}

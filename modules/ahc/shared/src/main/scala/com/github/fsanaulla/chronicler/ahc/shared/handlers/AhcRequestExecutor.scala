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

import com.github.fsanaulla.chronicler.core.components.RequestExecutor
import com.github.fsanaulla.chronicler.core.gzip
import com.softwaremill.sttp.{
  asByteArray,
  emptyRequest,
  HeaderNames,
  MediaTypes,
  Response,
  SttpBackend,
  Uri
}

import scala.concurrent.Future

private[ahc] final class AhcRequestExecutor()(implicit backend: SttpBackend[Future, Nothing])
  extends RequestExecutor[Future, Response[Array[Byte]], Uri, String] {

  /**
    * Execute uri
    *
    * @param uri - request uri
    * @return    - Return wrapper response
    */
  override def get(uri: Uri, compress: Boolean): Future[Response[Array[Byte]]] = {
    val request = emptyRequest.get(uri)

    // currently not supported
//    val maybeEncoded =
//      if (compress) request.acceptEncoding("gzip")
//      else request

    request
      .response(asByteArray)
      .send()
  }

  override def post(
      uri: Uri,
      body: String,
      compress: Boolean
    ): Future[Response[Array[Byte]]] = {
    val req = emptyRequest.post(uri).response(asByteArray)
    val maybeEncoded = if (compress) {
      val (length, data) = gzip.compress(body.getBytes())
      // it fails with input stream, using byte array instead
      req
        .body(data)
        .header(HeaderNames.ContentEncoding, "gzip", replaceExisting = true)
        .contentType(MediaTypes.Binary)
        .contentLength(length)
    } else req.body(body)

    maybeEncoded.send()
  }

  /**
    * Quite simple post operation for creating
    *
    * @param uri - request uri
    */
  override def post(uri: Uri): Future[Response[Array[Byte]]] = {
    emptyRequest
      .post(uri)
      .response(asByteArray)
      .send()
  }
}

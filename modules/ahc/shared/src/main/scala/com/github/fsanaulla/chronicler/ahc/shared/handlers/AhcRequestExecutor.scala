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

import com.github.fsanaulla.chronicler.ahc.shared.alias._
import com.github.fsanaulla.chronicler.ahc.shared.formats._
import com.github.fsanaulla.chronicler.core.encoding.gzipEncoding
import com.github.fsanaulla.chronicler.core.typeclasses.RequestExecutor
import com.softwaremill.sttp.{Response, SttpBackend, Uri, sttp}
import jawn.ast.JValue

import scala.concurrent.Future

private[ahc] final class AhcRequestExecutor(implicit backend: SttpBackend[Future, Nothing])
  extends RequestExecutor[Future, Request, Response[JValue], Uri, String] {

  /**
    * Execute uri
    *
    * @param uri - request uri
    * @return    - Return wrapper response
    */
  override def executeUri(uri: Uri): Future[Response[JValue]] =
    sttp.get(uri).response(asJson).send()

  /**
    * Execute request
    *
    * @param req - request
    * @return - Return wrapper response
    */
  override def executeReq(req: Request): Future[Response[JValue]] =
    req.send()

  override def execute(uri: Uri, body: String, gzipped: Boolean): Future[Response[JValue]] = {
    val req = sttp.post(uri).body(body).response(asJson)
    val maybeEncoded = if (gzipped) req.acceptEncoding(gzipEncoding) else req
    maybeEncoded.send()
  }
}

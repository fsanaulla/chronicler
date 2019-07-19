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

package com.github.fsanaulla.chronicler.akka.shared.handlers

import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.github.fsanaulla.chronicler.akka.shared.formats._
import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.components.RequestExecutor
import com.github.fsanaulla.chronicler.core.encoding.gzipEncoding
import com.softwaremill.sttp.{sttp, Response, ResponseAs, SttpBackend, Uri}
import org.typelevel.jawn.ast.JValue

import scala.concurrent.Future
import scala.language.higherKinds

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 15.03.18
  */
private[akka] final class AkkaRequestExecutor(
  )(implicit backend: SttpBackend[Future, Source[ByteString, Any]])
  extends RequestExecutor[Future, Response[JValue], Uri, String] {

  private[this] def executeGet[F[_], V, S](
      uri: Uri,
      responseAs: ResponseAs[V, S]
    )(implicit backend: SttpBackend[F, S]
    ): F[Response[V]] =
    sttp.get(uri).response(responseAs).send()

  /**
    * Execute uri
    *
    * @param uri - request uri
    * @return    - Return wrapper response
    */
  override def get(uri: Uri): Future[Response[JValue]] =
    executeGet(uri, asJson)(backend)

  override def post(
      uri: Uri,
      body: String,
      gzipped: Boolean
    ): Future[Response[JValue]] = {
    val req          = sttp.post(uri).body(body).response(asJson)
    val maybeEncoded = if (gzipped) req.acceptEncoding(gzipEncoding) else req
    maybeEncoded.send()
  }

  /**
    * Receive chunked response
    *
    * @param uri - request uri
    * @since 0.5.4
    */
  def getStream(uri: Uri): Future[Response[Source[ErrorOr[JValue], Any]]] =
    executeGet(uri, asJvSource)
}

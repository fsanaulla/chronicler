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

import akka.http.scaladsl.HttpExt
import akka.http.scaladsl.model._
import akka.stream.scaladsl.{Framing, Source}
import akka.util.ByteString
import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.components.RequestExecutor
import com.github.fsanaulla.chronicler.core.jawn._
import org.typelevel.jawn.ast.{JParser, JValue}

import scala.concurrent.Future
import scala.language.higherKinds

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 15.03.18
  */
private[akka] final class AkkaRequestExecutor()(implicit http: HttpExt)
  extends RequestExecutor[Future, HttpResponse, Uri, RequestEntity] {

//  private[this] def executeGet[F[_], V, S](
//      uri: Uri,
//      responseAs: ResponseAs[V, S]
//    )(implicit backend: SttpBackend[F, S]
//    ): F[Response[V]] =
//    sttp.get(uri).response(responseAs).send()

  /**
    * Execute uri
    *
    * @param uri - request uri
    * @return    - Return wrapper response
    */
  override def get(uri: Uri): Future[HttpResponse] =
    http.singleRequest(HttpRequest(method = HttpMethods.GET, uri))

  override def post(
      uri: Uri,
      body: RequestEntity,
      gzipped: Boolean
    ): Future[HttpResponse] = {
    http.singleRequest(
      HttpRequest(
        HttpMethods.POST,
        uri,
        entity = body
      )
    )
  }

  /**
    * Receive chunked response
    *
    * @param uri - request uri
    * @since 0.5.4
    */
  def getStream(uri: Uri): Future[HttpResponse] =
    get(uri)
}

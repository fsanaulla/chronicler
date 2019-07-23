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

import akka.http.scaladsl.model._
import akka.http.scaladsl.{HttpExt, HttpsConnectionContext}
import com.github.fsanaulla.chronicler.core.components.RequestExecutor

import scala.concurrent.Future
import scala.language.higherKinds

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 15.03.18
  */
private[akka] final class AkkaRequestExecutor(ctx: HttpsConnectionContext)(implicit http: HttpExt)
  extends RequestExecutor[Future, HttpResponse, Uri, RequestEntity] {

  /**
    * Execute uri
    *
    * @param uri - request uri
    * @return    - Return wrapper response
    */
  override def get(uri: Uri): Future[HttpResponse] =
    http.singleRequest(
      HttpRequest(method = HttpMethods.GET, uri),
      connectionContext = ctx
    )

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
      ),
      connectionContext = ctx
    )
  }
}

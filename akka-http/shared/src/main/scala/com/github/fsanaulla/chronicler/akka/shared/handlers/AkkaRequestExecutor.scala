/*
 * Copyright 2017-2018 Faiaz Sanaulla
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

import _root_.akka.http.scaladsl.model._
import _root_.akka.stream.ActorMaterializer
import _root_.akka.stream.scaladsl.{Sink, Source}
import com.github.fsanaulla.chronicler.akka.shared.utils.AkkaAlias.Connection
import com.github.fsanaulla.chronicler.core.typeclasses.RequestExecutor

import scala.concurrent.Future
import scala.language.implicitConversions

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 15.03.18
  */
private[akka] trait AkkaRequestExecutor
  extends RequestExecutor[Future, HttpRequest, HttpResponse, Uri] {

  private[akka] implicit val mat: ActorMaterializer
  private[akka] implicit val connection: Connection

  private[chronicler] override implicit def buildRequest(uri: Uri): HttpRequest =
    HttpRequest(uri = uri)

  private[chronicler] override def execute(request: HttpRequest): Future[HttpResponse] =
    Source.single(request).via(connection).runWith(Sink.head)
}

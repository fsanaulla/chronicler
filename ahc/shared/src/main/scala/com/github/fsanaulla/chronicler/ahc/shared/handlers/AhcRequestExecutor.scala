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

package com.github.fsanaulla.chronicler.ahc.shared.handlers

import com.github.fsanaulla.chronicler.ahc.shared.alias._
import com.github.fsanaulla.chronicler.ahc.shared.formats._
import com.github.fsanaulla.chronicler.core.typeclasses.RequestExecutor
import com.softwaremill.sttp.{Response, SttpBackend, Uri, sttp}
import jawn.ast.JValue

import scala.concurrent.Future
import scala.language.implicitConversions

private[ahc] trait AhcRequestExecutor extends RequestExecutor[Future, Request, Response[JValue], Uri] {
  private[ahc] implicit val backend: SttpBackend[Future, Nothing]

  private[chronicler] override implicit def buildRequest(uri: Uri): Request = sttp.get(uri).response(asJson)

  private[chronicler] override def execute(request: Request): Future[Response[JValue]] = request.send()
}

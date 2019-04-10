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

import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.typeclasses.RequestExecutor
import com.github.fsanaulla.chronicler.urlhttp.shared.alias.Request
import com.github.fsanaulla.chronicler.urlhttp.shared.formats.asJson
import com.softwaremill.sttp.{EitherBackend, Id, Response, SttpBackend, Uri, sttp}
import jawn.ast.JValue

import scala.language.implicitConversions

private[urlhttp] class UrlRequestExecutor(implicit backend: EitherBackend[SttpBackend[Id, Nothing]])
  extends RequestExecutor[ErrorOr, Request, Response[JValue], Uri] {


  override implicit def makeRequest(uri: Uri): Request = sttp.get(uri).response(asJson)
  override def executeRequest(request: Request): ErrorOr[Response[JValue]] = request.send()
}

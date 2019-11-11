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

import java.nio.ByteBuffer

import com.github.fsanaulla.chronicler.core.alias.{ErrorOr, Id}
import com.github.fsanaulla.chronicler.core.components.JsonHandler
import com.github.fsanaulla.chronicler.core.gzip
import com.github.fsanaulla.chronicler.core.implicits.functorId
import com.github.fsanaulla.chronicler.core.jawn.RichJParser
import org.typelevel.jawn.ast.{JParser, JValue}
import requests.Response

final class UrlJsonHandler(compressed: Boolean) extends JsonHandler[Id, Response] {
//  private[this] def body(response: Response): Either[Throwable, JValue] = {
//    val bts  = response.contents
//    val data = if (compressed) gzip.decompress(bts) else bts
//    JParser.parseFromByteBufferEither(ByteBuffer.wrap(data))
//  }

  override def responseBody(response: Response): ErrorOr[JValue] = {
    val bts  = response.contents
    val data = if (compressed) gzip.decompress(bts) else bts
    JParser.parseFromByteBufferEither(ByteBuffer.wrap(data))
  }

  override def responseHeader(response: Response): Seq[(String, String)] =
    response.headers.mapValues(_.head).toList

  override def responseCode(response: Response): Int =
    response.statusCode
}

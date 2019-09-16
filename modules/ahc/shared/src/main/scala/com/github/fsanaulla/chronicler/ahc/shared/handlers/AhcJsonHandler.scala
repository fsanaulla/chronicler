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

import java.nio.ByteBuffer
import java.nio.charset.{Charset, StandardCharsets}

import com.github.fsanaulla.chronicler.core.alias.{ErrorOr, Id}
import com.github.fsanaulla.chronicler.core.components.JsonHandler
import com.github.fsanaulla.chronicler.core.either.EitherOps
import com.github.fsanaulla.chronicler.core.encoding.encodingFromContentType
import com.github.fsanaulla.chronicler.core.implicits._
import com.github.fsanaulla.chronicler.core.jawn.RichJParser
import com.softwaremill.sttp.Response
import org.typelevel.jawn.ast.{JParser, JValue}

private[ahc] final class AhcJsonHandler(compress: Boolean)
  extends JsonHandler[Id, Response[Array[Byte]]] {

  def responseBody(response: Response[Array[Byte]]): ErrorOr[JValue] = {
    val ethBts = response.rawErrorBody
//    val maybeDecompressed = if (compress) ethBts.mapRight(gzip.decompress) else ethBts

    val encoding: Charset = response.contentType
      .flatMap(encodingFromContentType)
      .map(Charset.forName)
      .getOrElse(StandardCharsets.UTF_8)

    ethBts
      .mapRight(new String(_, encoding))
      .mapRight(JParser.parseFromStringOrNull)
      .flatMapLeft { bt =>
        val btBuff = ByteBuffer.wrap(bt)
        JParser.parseFromByteBufferEither(ByteBuffer.wrap(bt))
      }
  }

  def responseHeader(response: Response[Array[Byte]]): Seq[(String, String)] =
    response.headers

  def responseCode(response: Response[Array[Byte]]): Int =
    response.code
}

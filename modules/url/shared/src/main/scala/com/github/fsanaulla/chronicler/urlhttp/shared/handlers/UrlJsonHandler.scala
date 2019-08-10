package com.github.fsanaulla.chronicler.urlhttp.shared.handlers

import java.nio.ByteBuffer
import java.nio.charset.{Charset, StandardCharsets}

import com.github.fsanaulla.chronicler.core.alias.{ErrorOr, Id}
import com.github.fsanaulla.chronicler.core.components.JsonHandler
import com.github.fsanaulla.chronicler.core.encoding.encodingFromContentType
import com.github.fsanaulla.chronicler.core.gzip
import com.github.fsanaulla.chronicler.core.implicits.functorId
import com.github.fsanaulla.chronicler.core.jawn.RichJParser
import org.typelevel.jawn.ast.{JParser, JValue}
import requests.Response

final class UrlJsonHandler(compressed: Boolean) extends JsonHandler[Id, Response] {
  private[this] def body(response: Response, enc: Charset): Either[Throwable, JValue] = {
    val bts  = response.contents
    val data = if (compressed) gzip.decompress(bts) else bts
    JParser.parseFromByteBufferEither(ByteBuffer.wrap(data))
  }

  override def responseBody(response: Response): ErrorOr[JValue] = {
    response.contentType
      .flatMap(encodingFromContentType)
      .map(Charset.forName)
      .fold(body(response, StandardCharsets.UTF_8))(body(response, _))
  }

  override def responseHeader(response: Response): Seq[(String, String)] =
    response.headers.mapValues(_.head).toList

  override def responseCode(response: Response): Int =
    response.statusCode
}

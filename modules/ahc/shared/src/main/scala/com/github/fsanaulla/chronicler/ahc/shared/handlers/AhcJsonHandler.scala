package com.github.fsanaulla.chronicler.ahc.shared.handlers

import java.nio.charset.{Charset, StandardCharsets}

import com.github.fsanaulla.chronicler.core.alias.{ErrorOr, Id}
import com.github.fsanaulla.chronicler.core.components.JsonHandler
import com.github.fsanaulla.chronicler.core.either.EitherOps
import com.github.fsanaulla.chronicler.core.encoding.encodingFromContentType
import com.github.fsanaulla.chronicler.core.gzip
import com.github.fsanaulla.chronicler.core.implicits._
import com.github.fsanaulla.chronicler.core.jawn.RichJParser
import com.github.fsanaulla.chronicler.core.model.ParsingException
import com.softwaremill.sttp.Response
import org.typelevel.jawn.ast.{JParser, JValue}

private[ahc] final class AhcJsonHandler(compress: Boolean)
  extends JsonHandler[Id, Response[Array[Byte]]] {

  def responseBody(response: Response[Array[Byte]]): ErrorOr[JValue] = {
    val ethBts            = response.rawErrorBody
    val maybeDecompressed = if (compress) ethBts.mapRight(gzip.decompress) else ethBts

    val encoding: Charset = response.contentType
      .flatMap(encodingFromContentType)
      .map(Charset.forName)
      .getOrElse(StandardCharsets.UTF_8)

    maybeDecompressed match {
      case Right(value) =>
        JParser.parseFromStringEither(new String(value, encoding))
      case Left(bts) =>
        Left(new ParsingException(s"Can't parse: ${new String(bts, encoding)}"))
    }
  }

  def responseHeader(response: Response[Array[Byte]]): Seq[(String, String)] =
    response.headers

  def responseCode(response: Response[Array[Byte]]): Int =
    response.code
}

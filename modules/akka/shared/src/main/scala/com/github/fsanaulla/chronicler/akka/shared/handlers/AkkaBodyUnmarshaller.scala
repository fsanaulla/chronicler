package com.github.fsanaulla.chronicler.akka.shared.handlers

import akka.http.scaladsl.coding.Gzip
import akka.http.scaladsl.model.{HttpCharsets, HttpEntity}
import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.stream.Materializer
import akka.util.ByteString
import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.jawn.RichJParser
import org.typelevel.jawn.ast.{JParser, JValue}

import scala.concurrent.{ExecutionContext, Future}

final class AkkaBodyUnmarshaller(compressed: Boolean)
  extends Unmarshaller[HttpEntity, ErrorOr[JValue]] {

  override def apply(
      value: HttpEntity
    )(implicit ec: ExecutionContext,
      mat: Materializer
    ): Future[ErrorOr[JValue]] = {

    // get encoding from response content type, otherwise use UTF-8 as default
    val encoding = value.contentType.charsetOption
      .getOrElse(HttpCharsets.`UTF-8`)
      .nioCharset()

    val srcBody = if (compressed) value.dataBytes.via(Gzip.decoderFlow) else value.dataBytes

    srcBody
      .runFold(ByteString.empty)(_ ++ _)
      .map(_.decodeString(encoding))
      .map(JParser.parseFromStringEither)
  }
}

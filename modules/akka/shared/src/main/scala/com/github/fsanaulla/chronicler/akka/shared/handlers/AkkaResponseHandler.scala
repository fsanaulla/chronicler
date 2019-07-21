package com.github.fsanaulla.chronicler.akka.shared.handlers

import akka.http.scaladsl.model.HttpResponse
import akka.stream.scaladsl.{Framing, Source}
import akka.util.ByteString
import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.components.{JsonHandler, ResponseHandler}
import com.github.fsanaulla.chronicler.core.either
import com.github.fsanaulla.chronicler.core.either.EitherOps
import com.github.fsanaulla.chronicler.core.model.InfluxReader
import com.github.fsanaulla.chronicler.core.jawn.RichJParser
import org.typelevel.jawn.ast.{JArray, JParser, JValue}

import scala.reflect.ClassTag

class AkkaResponseHandler(jsonHandler: JsonHandler[HttpResponse])
  extends ResponseHandler[HttpResponse](jsonHandler) {

  final def queryChunkedResultJson(response: HttpResponse): Source[ErrorOr[Array[JArray]], Any] = {
    response.entity.dataBytes
      .via(Framing.delimiter(ByteString("\n"), Int.MaxValue))
      .map(_.utf8String)
      .map(JParser.parseFromStringEither)
      .map(_.flatMapRight(jsonHandler.queryResult))
  }

  final def queryChunkedResult[T: ClassTag](
      response: HttpResponse
    )(implicit rd: InfluxReader[T]
    ): Source[ErrorOr[Array[T]], Any] = {
    queryChunkedResultJson(response)
      .map(_.flatMapRight { arr =>
        either.array(arr.map(rd.read))
      })
  }
}

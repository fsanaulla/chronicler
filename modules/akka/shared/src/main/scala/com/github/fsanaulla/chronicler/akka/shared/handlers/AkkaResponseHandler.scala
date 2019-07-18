package com.github.fsanaulla.chronicler.akka.shared.handlers

import akka.stream.scaladsl.Source
import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.components.{JsonHandler, ResponseHandler}
import com.github.fsanaulla.chronicler.core.either
import com.github.fsanaulla.chronicler.core.either.EitherOps
import com.github.fsanaulla.chronicler.core.model.InfluxReader
import com.softwaremill.sttp.Response
import org.typelevel.jawn.ast.{JArray, JValue}

import scala.reflect.ClassTag

class AkkaResponseHandler(jsonHandler: JsonHandler[Response[JValue]])
  extends ResponseHandler[Response[JValue]](jsonHandler) {

  final def queryChunkedResultJson(
      response: Response[Source[ErrorOr[JValue], Any]]
    ): ErrorOr[Source[ErrorOr[Array[JArray]], Any]] = {
    response.body match {
      case Left(_) =>
        Left(new IllegalArgumentException(s"Can't build for $response"))
      case Right(src) =>
        Right(src.map(_.flatMapRight(jsonHandler.queryResult)))
    }
  }

  final def queryChunkedResult[T: ClassTag](
      response: Response[Source[ErrorOr[JValue], Any]]
    )(implicit rd: InfluxReader[T]
    ): ErrorOr[Source[ErrorOr[Array[T]], Any]] = {
    queryChunkedResultJson(response).mapRight { src =>
      src.map { eth =>
        eth.flatMapRight { arr =>
          either.array(arr.map(rd.read))
        }
      }
    }
  }
}

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

package com.github.fsanaulla.chronicler.akka.io

import akka.stream.scaladsl.Source
import com.github.fsanaulla.chronicler.akka.shared.{ResponseE, ResponseS}
import com.github.fsanaulla.chronicler.core.alias.{ErrorOr, Id}
import com.github.fsanaulla.chronicler.core.components.{JsonHandler, ResponseHandlerBase}
import com.github.fsanaulla.chronicler.core.either
import com.github.fsanaulla.chronicler.core.either.EitherOps
import com.github.fsanaulla.chronicler.core.jawn.RichJParser
import com.github.fsanaulla.chronicler.core.model.{InfluxReader, ParsingException}
import org.typelevel.jawn.ast.{JArray, JParser}

import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag

final class AkkaResponseHandler(jsonHandler: JsonHandler[Id, ResponseE])(
    implicit ec: ExecutionContext
) extends ResponseHandlerBase[Id, ResponseE](jsonHandler) {

  def queryChunkedResultJson(
      response: ResponseS
  ): Source[ErrorOr[Array[JArray]], Any] = {
    val body = response.body

    val src = body.left.map(new Exception(_)).right.map { source =>
      source.map { bs =>
        val str  = bs.utf8String
        val json = JParser.parseFromStringEither(str)
        val rows = json.flatMapRight { js =>
          jsonHandler
            .queryResult(js)
            .toRight[Throwable](
              new ParsingException("Can't extract query result from response")
            )
        }

        rows
      }
    }

    src match {
      case Left(ex) => Source.failed(ex)
      case Right(s) => s
    }
  }

  def queryChunkedResult[T: ClassTag](
      response: ResponseS
  )(implicit rd: InfluxReader[T]): Source[Either[Throwable, Array[T]], Any] = {
    queryChunkedResultJson(response).map { eth =>
      eth.flatMapRight { arr =>
        either.array(arr.map(rd.read))
      }
    }
  }
}

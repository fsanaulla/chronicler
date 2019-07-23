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

package com.github.fsanaulla.chronicler.akka.shared.handlers

import akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import akka.http.scaladsl.unmarshalling.{Unmarshal, Unmarshaller}
import akka.stream.ActorMaterializer
import akka.util.ByteString
import com.github.fsanaulla.chronicler.akka.shared.implicits.futureFunctor
import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.components.JsonHandler
import com.github.fsanaulla.chronicler.core.jawn.RichJParser
import org.typelevel.jawn.ast.{JParser, JValue}

import scala.concurrent.{ExecutionContext, Future}

final class AkkaJsonHandler()(implicit ex: ExecutionContext, mat: ActorMaterializer)
  extends JsonHandler[Future, HttpResponse] {

  /** Custom Unmarshaller for Jawn JSON */
  implicit val unm: Unmarshaller[HttpEntity, ErrorOr[JValue]] = {
    Unmarshaller.withMaterializer { implicit ex => implicit mat => entity: HttpEntity =>
      entity.dataBytes
        .runFold(ByteString.empty)(_ ++ _)
        .map(db => JParser.parseFromStringEither(db.utf8String))
    }
  }

  override def responseBody(response: HttpResponse): Future[ErrorOr[JValue]] =
    Unmarshal(response.entity).to[ErrorOr[JValue]]

  override def responseHeader(response: HttpResponse): Seq[(String, String)] =
    response.headers.map(hd => hd.name() -> hd.value())

  override def responseCode(response: HttpResponse): Int =
    response.status.intValue()
}

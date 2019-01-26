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

import _root_.akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import _root_.akka.http.scaladsl.unmarshalling.{Unmarshal, Unmarshaller}
import _root_.akka.stream.ActorMaterializer
import _root_.akka.util.ByteString
import com.github.fsanaulla.chronicler.core.jawn._
import com.github.fsanaulla.chronicler.core.model.Executable
import com.github.fsanaulla.chronicler.core.typeclasses.JsonHandler
import jawn.ast.{JParser, JValue}

import scala.concurrent.Future

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 15.03.18
  */
private[akka] trait AkkaJsonHandler extends JsonHandler[Future, HttpResponse] with Executable {

  private[akka] implicit val mat: ActorMaterializer

  /** Custom Unmarshaller for Jawn JSON */
  private implicit val unm: Unmarshaller[HttpEntity, JValue] = {
    Unmarshaller.withMaterializer {
      implicit ex =>
        implicit mat =>
          entity: HttpEntity =>
            entity.dataBytes
              .runFold(ByteString.empty)(_ ++ _)
              .flatMap(db => Future.fromTry(JParser.parseFromString(db.utf8String)))
    }
  }

  private[chronicler] override def getResponseBody(response: HttpResponse): Future[JValue] =
    Unmarshal(response.entity).to[JValue]

  private[chronicler]override def getResponseError(response: HttpResponse): Future[String] =
    getResponseBody(response).map(_.get("error").asString)

  private[chronicler]override def getOptResponseError(response: HttpResponse): Future[Option[String]] =
    getResponseBody(response)
      .map(_.get("results").arrayValue.flatMap(_.headOption))
      .map(_.flatMap(_.get("error").getString))
}

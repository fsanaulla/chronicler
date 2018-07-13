/*
 * Copyright 2017-2018 Faiaz Sanaulla
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

package com.github.fsanaulla.chronicler.async.handlers

import com.github.fsanaulla.chronicler.core.handlers.JsonHandler
import com.github.fsanaulla.chronicler.core.model.Executable
import com.github.fsanaulla.chronicler.core.utils.Extensions.RichJValue
import com.softwaremill.sttp.Response
import jawn.ast.{JParser, JValue}

import scala.concurrent.Future

private[async] trait AsyncJsonHandler
    extends JsonHandler[Future, Response[JValue]]
    with Executable {

  override def getResponseBody(response: Response[JValue]): Future[JValue] = {
    response.body match {
      case Right(js) => Future.successful(js)
      case Left(str) => Future.fromTry(JParser.parseFromString(str))
    }
  }

  override def getResponseError(response: Response[JValue]): Future[String] =
    getResponseBody(response).map(_.get("error").asString)

  override def getOptResponseError(response: Response[JValue]): Future[Option[String]] =
    getResponseBody(response)
      .map(_.get("results").arrayValue.flatMap(_.headOption))
      .map(_.flatMap(_.get("error").getString))

}

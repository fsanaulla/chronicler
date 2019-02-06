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

package com.github.fsanaulla.chronicler.urlhttp.shared.handlers

import com.github.fsanaulla.chronicler.core.jawn._
import com.github.fsanaulla.chronicler.core.typeclasses.JsonHandler
import com.softwaremill.sttp.Response
import jawn.ast.{JParser, JValue}

import scala.util.{Success, Try}

private[urlhttp] trait UrlJsonHandler extends JsonHandler[Try, Response[JValue]] {

  private[chronicler] override def getResponseBody(response: Response[JValue]): Try[JValue] = response.body match {
    case Right(js) => Success(js)
    case Left(str) => JParser.parseFromString(str)
  }

  private[chronicler] override def getResponseError(response: Response[JValue]): Try[String] =
    getResponseBody(response).map(_.get("error").asString)

  private[chronicler] override def getOptResponseError(response: Response[JValue]): Try[Option[String]] =
    getResponseBody(response)
      .map(_.get("results").arrayValue.flatMap(_.headOption))
      .map(_.flatMap(_.get("error").getString))

}

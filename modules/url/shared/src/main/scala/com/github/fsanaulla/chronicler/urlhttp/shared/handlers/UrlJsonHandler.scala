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

import com.github.fsanaulla.chronicler.core.headers._
import com.github.fsanaulla.chronicler.core.jawn._
import com.github.fsanaulla.chronicler.core.model.HeaderNotFoundException
import com.github.fsanaulla.chronicler.core.typeclasses.JsonHandler
import com.github.fsanaulla.chronicler.urlhttp.shared.implicits._
import com.softwaremill.sttp.Response
import jawn.ast.{JParser, JValue}

import scala.util.{Failure, Success, Try}

private[urlhttp] final class UrlJsonHandler extends JsonHandler[Try, Response[JValue]] {

  private[chronicler] override def pingHeaders(response: Response[JValue]): Try[(String, String)] = {
    val headers = response.headers
    val result = for {
      build   <- headers.find(_._1 == buildHeader).map(_._2)
      version <- headers.find(_._1 == versionHeader).map(_._2)
    } yield build -> version

    result.toSuccess(Failure(new HeaderNotFoundException(s"Can't find $buildHeader or $versionHeader")))
  }

  private[chronicler] override def responseBody(response: Response[JValue]): Try[JValue] =
    response.body match {
      case Right(js) => Success(js)
      case Left(str) => JParser.parseFromString(str)
    }

  private[chronicler] override def responseError(response: Response[JValue]): Try[String] =
    responseBody(response).map(_.get("error").asString)

  private[chronicler] override def responseErrorOpt(response: Response[JValue]): Try[Option[String]] =
    responseBody(response)
      .map(_.get("results").arrayValue.flatMap(_.headOption))
      .map(_.flatMap(_.get("error").getString))

}

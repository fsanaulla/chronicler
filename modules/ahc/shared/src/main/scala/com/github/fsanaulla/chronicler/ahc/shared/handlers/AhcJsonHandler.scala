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

package com.github.fsanaulla.chronicler.ahc.shared.handlers

import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.headers.{buildHeader, versionHeader}
import com.github.fsanaulla.chronicler.core.jawn._
import com.github.fsanaulla.chronicler.core.model.{HeaderNotFoundException, InfluxDBInfo}
import com.github.fsanaulla.chronicler.core.typeclasses.JsonHandler
import com.softwaremill.sttp.Response
import jawn.ast.{JParser, JValue}

import scala.concurrent.{ExecutionContext, Future}

private[ahc] class AhcJsonHandler(implicit ex: ExecutionContext) extends JsonHandler[Future, Response[JValue]] {

  override def pingHeaders(response: Response[JValue]): ErrorOr[InfluxDBInfo] = {
    val headers = response.headers
    val result = for {
      build   <- headers.collectFirst { case (k, v) if k == buildHeader   => v }
      version <- headers.collectFirst { case (k, v) if k == versionHeader => v }
    } yield InfluxDBInfo(build, version)

    result.toRight(new HeaderNotFoundException(s"Can't find $buildHeader or $versionHeader"))
  }

  override def responseBody(response: Response[JValue]): Future[JValue] = {
    response.body match {
      case Right(js) => Future.successful(js)
      case Left(str) =>
        Future.fromTry(JParser.parseFromString(str))
    }
  }

  override def responseError(response: Response[JValue]): Future[String] =
    responseBody(response).map(_.get("error").asString)

  override def responseErrorOpt(response: Response[JValue]): Future[Option[String]] =
    responseBody(response)
      .map(_.get("results").arrayValue.flatMap(_.headOption))
      .map(_.flatMap(_.get("error").getString))

}

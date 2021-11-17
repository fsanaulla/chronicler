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

package com.github.fsanaulla.chronicler.async.shared

import com.github.fsanaulla.chronicler.async.shared.ResponseE
import com.github.fsanaulla.chronicler.core.alias.{ErrorOr, Id}
import com.github.fsanaulla.chronicler.core.components.JsonHandler
import org.typelevel.jawn.ast.{JParser, JValue}

import scala.util.{Failure, Success}

private[async] final class AsyncJsonHandler extends JsonHandler[Id, ResponseE] {

  /***
    * Extract response http code
    */
  override def responseCode(response: ResponseE): Int =
    response.code.code

  /***
    * Extract response headers
    */
  override def responseHeader(response: ResponseE): Seq[(String, String)] =
    response.headers.map(h => h.name -> h.value)

  /** *
    * Extracting JSON from Response
    *
    * @param response - HTTP response
    */
  override def responseBody(response: ResponseE): ErrorOr[JValue] = {
    response.body match {
      case Left(err) =>
        Left(new Exception(s"Unable to extract response body. Message: $err"))
      case Right(body) =>
        // sttp will automatically decode gzipped response
        JParser.parseFromString(body) match {
          case Failure(exception) => Left(exception)
          case Success(value)     => Right(value)
        }
    }
  }
}

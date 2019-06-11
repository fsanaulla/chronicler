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

package com.github.fsanaulla.chronicler.urlhttp.shared

import java.nio.charset.Charset

import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.components.JsonHandler
import com.github.fsanaulla.chronicler.core.encoding.encodingFromContentType
import com.github.fsanaulla.chronicler.core.model.{Failable, Functor}
import jawn.ast.{JParser, JValue}
import requests.Response

import scala.util.{Failure, Success, Try}

package object implicits {
  implicit val jsonHandler: JsonHandler[Response] = new JsonHandler[Response] {
    override def responseBody(response: Response): ErrorOr[JValue] = {
      def body(enc: String): Either[Throwable, JValue] = {
        JParser.parseFromString(response.text(Charset.forName(enc))) match {
          case Success(value)     => Right(value)
          case Failure(exception) => Left(exception)
        }
      }
      response
        .contentType
        .flatMap(encodingFromContentType)
        .fold(body("UTF-8"))(body)
    }

    override def responseHeader(response: Response): Seq[(String, String)] =
      response.headers.mapValues(_.head).toSeq

    override def responseCode(response: Response): Int =
      response.statusCode
  }

  implicit val tryFunctor: Functor[Try] = new Functor[Try] {
    override def map[A, B](fa: Try[A])(f: A => B): Try[B] = fa.map(f)
  }

  implicit def tryFailable: Failable[Try] = new Failable[Try] {
    override def fail[A](ex: Throwable): Try[A] = Failure(ex)
  }
}

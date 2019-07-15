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

package com.github.fsanaulla.chronicler.akka.shared

import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.components.JsonHandler
import com.github.fsanaulla.chronicler.core.model.{Failable, Functor}
import com.softwaremill.sttp.Response
import org.typelevel.jawn.ast.{JParser, JValue}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

package object implicits {
  implicit val jsonHandler: JsonHandler[Response[JValue]] = new JsonHandler[Response[JValue]] {
    override def responseBody(response: Response[JValue]): ErrorOr[JValue] =
      response.body // fallback encoding utf-8
      .left
        .flatMap { str =>
          JParser.parseFromString(str) match {
            case Success(value)     => Right(value)
            case Failure(exception) => Left(exception)
          }
        }

    override def responseHeader(response: Response[JValue]): Seq[(String, String)] =
      response.headers

    override def responseCode(response: Response[JValue]): Int =
      response.code
  }

  implicit def futureFunctor(implicit ec: ExecutionContext): Functor[Future] = new Functor[Future] {
    override def map[A, B](fa: Future[A])(f: A => B): Future[B] = fa.map(f)
  }

  implicit def futureFailable: Failable[Future] = new Failable[Future] {
    override def fail[A](ex: Throwable): Future[A] = Future.failed(ex)
  }
}

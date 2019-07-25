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

package com.github.fsanaulla.chronicler.ahc.shared

import com.github.fsanaulla.chronicler.core.alias.{ErrorOr, Id}
import com.github.fsanaulla.chronicler.core.components.JsonHandler
import com.github.fsanaulla.chronicler.core.implicits.functorId
import com.github.fsanaulla.chronicler.core.jawn.RichJParser
import com.github.fsanaulla.chronicler.core.model.{Apply, Failable, FunctionK, Functor}
import com.softwaremill.sttp.Response
import org.typelevel.jawn.ast.{JParser, JValue}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}

package object implicits {

  implicit val jsonHandler: JsonHandler[Id, Response[JValue]] =
    new JsonHandler[Id, Response[JValue]] {

      def responseBody(response: Response[JValue]): ErrorOr[JValue] =
        response.body.left.flatMap(JParser.parseFromStringEither)

      def responseHeader(response: Response[JValue]): Seq[(String, String)] =
        response.headers

      def responseCode(response: Response[JValue]): Int =
        response.code
    }

  implicit def futureFunctor(implicit ec: ExecutionContext): Functor[Future] = new Functor[Future] {
    override def map[A, B](fa: Future[A])(f: A => B): Future[B] = fa.map(f)

    override def flatMap[A, B](fa: Future[A])(f: A => Future[B]): Future[B] = fa.flatMap(f)
  }

  implicit def futureFailable: Failable[Future] = new Failable[Future] {
    override def fail[A](ex: Throwable): Future[A] = Future.failed(ex)
  }

  implicit val futureApply: Apply[Future] = new Apply[Future] {
    override def pure[A](v: A): Future[A] = Future.successful(v)
  }

  implicit val fkId: FunctionK[Id, Future] = new FunctionK[Id, Future] {
    override def apply[A](fa: Id[A]): Future[A] = Future.successful(fa)
  }
}

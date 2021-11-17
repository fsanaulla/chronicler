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

package com.github.fsanaulla.chronicler.async

import com.github.fsanaulla.chronicler.core.alias.Id
import com.github.fsanaulla.chronicler.core.typeclasses._
import sttp.client3.{RequestT, Response}

import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds

package object shared {
  type RequestE[F[_]] = RequestT[F, Either[String, String], Any]
  type ResponseE      = Response[Either[String, String]]

  implicit def futureFunctor(implicit ec: ExecutionContext): Functor[Future] = new Functor[Future] {
    override def map[A, B](fa: Future[A])(f: A => B): Future[B] = fa.map(f)
  }

  implicit def futureMonadError(implicit ec: ExecutionContext): MonadError[Future, Throwable] =
    new MonadError[Future, Throwable] {
      override def fail[A](ex: Throwable): Future[A]                          = Future.failed(ex)
      override def flatMap[A, B](fa: Future[A])(f: A => Future[B]): Future[B] = fa.flatMap(f)
      override def pure[A](a: A): Future[A]                                   = Future.successful(a)
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

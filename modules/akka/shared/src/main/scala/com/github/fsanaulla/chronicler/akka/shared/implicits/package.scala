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

import com.github.fsanaulla.chronicler.core.model.WriteResult

import scala.concurrent.Future

package object implicits {
  implicit final class WriteResultOps(private val wr: WriteResult.type) extends AnyVal {
    def successfulFuture(code: Int): Future[WriteResult] = Future.successful(wr.successful(code))
  }

  implicit final class FutureOps(private val fut: Future.type) extends AnyVal {
    def fromOption[A](opt: Option[A])(ifNone: => Future[A]): Future[A] =
      opt.fold(ifNone)(Future.successful)
  }
}

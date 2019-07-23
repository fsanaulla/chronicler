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

import akka.actor.ActorSystem
import akka.http.scaladsl.{Http, HttpExt, HttpsConnectionContext}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

abstract class InfluxAkkaClient(
    terminateActorSystem: Boolean,
    httpsContext: Option[HttpsConnectionContext]
  )(implicit system: ActorSystem,
    ec: ExecutionContext) { self: AutoCloseable =>

  private[chronicler] implicit val http: HttpExt = Http()

  private[chronicler] val (ctx, schema) =
    httpsContext
      .map(_ -> "https")
      .getOrElse(http.defaultClientHttpsContext -> "http")

  def close(): Unit =
    Await.ready(closeAsync(), Duration.Inf)

  def closeAsync(): Future[Unit] = {
    for {
      _ <- http.shutdownAllConnectionPools()
      _ <- if (terminateActorSystem) system.terminate().map(_ => {}) else Future.unit
    } yield {}
  }
}

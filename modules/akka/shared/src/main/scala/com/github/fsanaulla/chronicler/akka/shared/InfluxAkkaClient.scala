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
import akka.http.scaladsl.{Http, HttpsConnectionContext}
import akka.stream.{ActorMaterializer, StreamTcpException}
import com.github.fsanaulla.chronicler.akka.shared.alias.Connection
import com.github.fsanaulla.chronicler.core.model.{ConnectionException, UnknownConnectionException}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

abstract class InfluxAkkaClient(host: String,
                                port: Int,
                                httpsContext: Option[HttpsConnectionContext])
                               (implicit system: ActorSystem) { self: AutoCloseable =>

  private[akka] implicit val mat: ActorMaterializer = ActorMaterializer()
  private[akka] implicit val connection: Connection =
    httpsContext.fold(Http().outgoingConnection(host, port)) { ctx =>
        Http().outgoingConnectionHttps(host, port, ctx)
      } recover {
        case ex: StreamTcpException => throw new ConnectionException(ex.getMessage)
        case unknown => throw new UnknownConnectionException(unknown.getMessage)
      }

  override def close(): Unit =
    Await.ready(closeAsync, Duration.Inf)

  def closeAsync: Future[Unit] =
    Http().shutdownAllConnectionPools()
}

/*
 * Copyright 2017-2018 Faiaz Sanaulla
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

package com.github.fsanaulla.chronicler.akka.management

import _root_.akka.http.scaladsl.Http
import _root_.akka.http.scaladsl.model.{HttpRequest, HttpResponse, RequestEntity, Uri}
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, StreamTcpException}
import com.github.fsanaulla.chronicler.akka.shared.handlers.{AkkaQueryBuilder, AkkaRequestExecutor, AkkaResponseHandler}
import com.github.fsanaulla.chronicler.akka.shared.utils.AkkaAlias.Connection
import com.github.fsanaulla.chronicler.core.ManagementClient
import com.github.fsanaulla.chronicler.core.model._
import com.github.fsanaulla.chronicler.core.typeclasses.FlatMap

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

final class AkkaManagementClient(host: String,
                                 port: Int,
                                 val credentials: Option[InfluxCredentials])
                                (implicit val ex: ExecutionContext, val system: ActorSystem)
  extends ManagementClient[Future, HttpRequest, HttpResponse, Uri, RequestEntity]
    with AkkaRequestExecutor
    with AkkaResponseHandler
    with AkkaQueryBuilder
    with HasCredentials
    with FlatMap[Future]
    with AutoCloseable {

  private[chronicler] override def flatMap[A, B](fa: Future[A])(f: A => Future[B]): Future[B] = fa.flatMap(f)

  private[akka] implicit val mat: ActorMaterializer = ActorMaterializer()
  private[akka] implicit val connection: Connection = Http()
    .outgoingConnection(host, port)
    .recover {
      case ex: StreamTcpException => throw new ConnectionException(ex.getMessage)
      case unknown => throw new UnknownConnectionException(unknown.getMessage)
    }

  override def close(): Unit =
    Await.ready(Http().shutdownAllConnectionPools(), Duration.Inf)

  override def ping: Future[WriteResult] =
    flatMap(execute(Uri("/ping")))(toResult)
}

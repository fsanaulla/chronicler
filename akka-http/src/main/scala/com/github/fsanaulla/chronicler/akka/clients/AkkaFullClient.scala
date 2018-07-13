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

package com.github.fsanaulla.chronicler.akka.clients

import _root_.akka.actor.ActorSystem
import _root_.akka.http.scaladsl.Http
import _root_.akka.http.scaladsl.model._
import _root_.akka.stream.{ActorMaterializer, StreamTcpException}
import com.github.fsanaulla.chronicler.akka.api.{Database, Measurement}
import com.github.fsanaulla.chronicler.akka.handlers.{AkkaQueryHandler, AkkaRequestHandler, AkkaResponseHandler}
import com.github.fsanaulla.chronicler.akka.utils.AkkaAlias.Connection
import com.github.fsanaulla.chronicler.core.client.FullClient
import com.github.fsanaulla.chronicler.core.model._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.reflect.ClassTag

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.08.17
  */
final class AkkaFullClient(host: String,
                           port: Int,
                           val credentials: Option[InfluxCredentials],
                           gzipped: Boolean)
                          (implicit val ex: ExecutionContext, val system: ActorSystem)
    extends FullClient[Future, HttpRequest, HttpResponse, Uri, RequestEntity]
      with AkkaRequestHandler
      with AkkaResponseHandler
      with AkkaQueryHandler
      with Mappable[Future, HttpResponse]
      with HasCredentials
      with AutoCloseable {

  private[chronicler] override def mapTo[B](resp: Future[HttpResponse],
                                            f: HttpResponse => Future[B]): Future[B] = resp.flatMap(f)

  private[akka] implicit val mat: ActorMaterializer = ActorMaterializer()
  private[akka] implicit val connection: Connection = Http().outgoingConnection(host, port) recover {
    case ex: StreamTcpException => throw new ConnectionException(ex.getMessage)
    case unknown => throw new UnknownConnectionException(unknown.getMessage)
  }

  override def database(dbName: String): Database =
    new Database(dbName, credentials, gzipped)

  override def measurement[A: ClassTag](dbName: String,
                               measurementName: String): Measurement[A] =
    new Measurement[A](dbName, measurementName, credentials, gzipped)

  override def ping: Future[WriteResult] =
    mapTo(execute(Uri("/ping")), toResult)

  override def close(): Unit =
    Await.ready(Http().shutdownAllConnectionPools(), Duration.Inf)
}

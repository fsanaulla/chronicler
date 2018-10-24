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

package com.github.fsanaulla.chronicler.akka.io

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.RequestEntity
import akka.stream.{ActorMaterializer, StreamTcpException}
import com.github.fsanaulla.chronicler.akka.io.api.{Database, Measurement}
import com.github.fsanaulla.chronicler.akka.shared.alias.Connection
import com.github.fsanaulla.chronicler.core.IOClient
import com.github.fsanaulla.chronicler.core.model.{ConnectionException, InfluxCredentials, UnknownConnectionException}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.reflect.ClassTag

final class AkkaIOClient(host: String,
                         port: Int,
                         val credentials: Option[InfluxCredentials],
                         gzipped: Boolean)
                        (implicit val ex: ExecutionContext, val system: ActorSystem)
  extends IOClient[Future, RequestEntity] {

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

  override def close(): Unit =
    Await.ready(Http().shutdownAllConnectionPools(), Duration.Inf)
}

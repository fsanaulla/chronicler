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

import _root_.akka.actor.ActorSystem
import _root_.akka.http.scaladsl.HttpsConnectionContext
import _root_.akka.http.scaladsl.model.{HttpRequest, HttpResponse, RequestEntity, Uri}
import com.github.fsanaulla.chronicler.akka.shared.InfluxAkkaClient
import com.github.fsanaulla.chronicler.akka.shared.handlers.{AkkaQueryBuilder, AkkaRequestExecutor, AkkaResponseHandler}
import com.github.fsanaulla.chronicler.core.ManagementClient
import com.github.fsanaulla.chronicler.core.model._
import com.github.fsanaulla.chronicler.core.typeclasses.FlatMap

import scala.concurrent.{ExecutionContext, Future}

final class AkkaManagementClient(host: String,
                                 port: Int,
                                 val credentials: Option[InfluxCredentials],
                                 httpsContext: Option[HttpsConnectionContext])
                                (implicit val ex: ExecutionContext, val system: ActorSystem)
  extends InfluxAkkaClient(host, port, httpsContext)
    with ManagementClient[Future, HttpRequest, HttpResponse, Uri, RequestEntity]
    with AkkaRequestExecutor
    with AkkaResponseHandler
    with AkkaQueryBuilder
    with HasCredentials
    with FlatMap[Future]
    with AutoCloseable {

  private[chronicler] override def flatMap[A, B](fa: Future[A])(f: A => Future[B]): Future[B] = fa.flatMap(f)

  override def ping: Future[WriteResult] =
    flatMap(execute(Uri("/ping")))(toResult)
}

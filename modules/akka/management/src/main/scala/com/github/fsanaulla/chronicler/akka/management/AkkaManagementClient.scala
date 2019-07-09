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

package com.github.fsanaulla.chronicler.akka.management

import _root_.akka.actor.ActorSystem
import _root_.akka.http.scaladsl.HttpsConnectionContext
import com.github.fsanaulla.chronicler.akka.shared.InfluxAkkaClient
import com.github.fsanaulla.chronicler.akka.shared.handlers.{AkkaQueryBuilder, AkkaRequestExecutor}
import com.github.fsanaulla.chronicler.akka.shared.implicits._
import com.github.fsanaulla.chronicler.core.ManagementClient
import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.components.ResponseHandler
import com.github.fsanaulla.chronicler.core.model._
import com.softwaremill.sttp.{Response, Uri}
import org.typelevel.jawn.ast.JValue

import scala.concurrent.{ExecutionContext, Future}

final class AkkaManagementClient(host: String,
                                 port: Int,
                                 val credentials: Option[InfluxCredentials],
                                 httpsContext: Option[HttpsConnectionContext])
                                (implicit val ex: ExecutionContext, val system: ActorSystem, val F: Functor[Future])
  extends InfluxAkkaClient(httpsContext)
    with ManagementClient[Future, Response[JValue], Uri, String] {

  implicit val qb: AkkaQueryBuilder = new AkkaQueryBuilder(host, port, credentials)
  implicit val re: AkkaRequestExecutor = new AkkaRequestExecutor
  implicit val rh: ResponseHandler[Response[JValue]] = new ResponseHandler(jsonHandler)

  override def ping: Future[ErrorOr[InfluxDBInfo]] = {
    re
      .get(qb.buildQuery("/ping", Map.empty[String, String]))
      .map(rh.pingResult)
  }
}

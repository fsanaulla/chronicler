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

package com.github.fsanaulla.chronicler.akka.io

import akka.actor.ActorSystem
import akka.http.scaladsl.HttpsConnectionContext
import com.github.fsanaulla.chronicler.akka.io.api.{Database, Measurement}
import com.github.fsanaulla.chronicler.akka.io.models.{AkkaReader, AkkaWriter}
import com.github.fsanaulla.chronicler.akka.shared.InfluxAkkaClient
import com.github.fsanaulla.chronicler.akka.shared.handlers.{AkkaQueryBuilder, AkkaRequestExecutor, AkkaResponseHandler}
import com.github.fsanaulla.chronicler.core.IOClient
import com.github.fsanaulla.chronicler.core.model.{InfluxCredentials, WriteResult}

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

final class AkkaIOClient(host: String,
                         port: Int,
                         credentials: Option[InfluxCredentials],
                         gzipped: Boolean,
                         httpsContext: Option[HttpsConnectionContext])
                        (implicit ex: ExecutionContext, system: ActorSystem)
  extends InfluxAkkaClient(httpsContext) with IOClient[Future, String] {

  implicit val qb: AkkaQueryBuilder = new AkkaQueryBuilder(host, port, credentials)
  implicit val re: AkkaRequestExecutor = new AkkaRequestExecutor
  implicit val rh: AkkaResponseHandler = new AkkaResponseHandler
  implicit val wr: AkkaWriter = new AkkaWriter
  implicit val rd: AkkaReader = new AkkaReader

  override def database(dbName: String): Database =
    new Database(dbName, gzipped)

  override def measurement[A: ClassTag](dbName: String, measurementName: String): Measurement[A] =
    new Measurement[A](dbName, measurementName, gzipped)

  override def ping: Future[WriteResult] =
    re
      .execute(re.buildRequest(qb.buildQuery("/ping", Map.empty[String, String])))
      .flatMap(rh.toResult)
}

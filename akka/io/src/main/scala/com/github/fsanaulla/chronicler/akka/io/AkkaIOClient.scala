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
import akka.http.scaladsl.HttpsConnectionContext
import akka.http.scaladsl.model.RequestEntity
import com.github.fsanaulla.chronicler.akka.io.api.{Database, Measurement}
import com.github.fsanaulla.chronicler.akka.shared.InfluxAkkaClient
import com.github.fsanaulla.chronicler.core.IOClient
import com.github.fsanaulla.chronicler.core.model.InfluxCredentials

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

final class AkkaIOClient(host: String,
                         port: Int,
                         val credentials: Option[InfluxCredentials],
                         gzipped: Boolean,
                         httpsContext: Option[HttpsConnectionContext])
                        (implicit val ex: ExecutionContext, val system: ActorSystem)
  extends InfluxAkkaClient(host, port, httpsContext) with IOClient[Future, RequestEntity] {

  override def database(dbName: String): Database =
    new Database(dbName, credentials, gzipped)

  override def measurement[A: ClassTag](dbName: String, measurementName: String): Measurement[A] =
    new Measurement[A](dbName, measurementName, credentials, gzipped)
}

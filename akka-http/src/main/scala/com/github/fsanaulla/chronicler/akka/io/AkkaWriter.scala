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

import java.nio.file.Paths

import _root_.akka.http.scaladsl.model._
import _root_.akka.stream.ActorMaterializer
import _root_.akka.stream.scaladsl.FileIO
import com.github.fsanaulla.chronicler.akka.handlers.{AkkaQueryHandler, AkkaRequestHandler, AkkaResponseHandler}
import com.github.fsanaulla.chronicler.akka.utils.AkkaAlias.Connection
import com.github.fsanaulla.chronicler.akka.utils.AkkaHeaders
import com.github.fsanaulla.chronicler.core.enums.{Consistency, Precision}
import com.github.fsanaulla.chronicler.core.io.WriteOperations
import com.github.fsanaulla.chronicler.core.model.{Executable, HasCredentials, WriteResult}
import com.github.fsanaulla.chronicler.core.query.DatabaseOperationQuery

import scala.concurrent.Future

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 03.09.17
  */
private[akka] trait AkkaWriter
  extends DatabaseOperationQuery[Uri]
    with AkkaRequestHandler
    with AkkaResponseHandler
    with AkkaQueryHandler
    with WriteOperations[Future, RequestEntity] { self: HasCredentials with Executable =>

  private[akka] implicit val mat: ActorMaterializer
  private[akka] implicit val connection: Connection

  private[chronicler] override def writeTo(dbName: String,
                                           entity: RequestEntity,
                                           consistency: Consistency,
                                           precision: Precision,
                                           retentionPolicy: Option[String],
                                           gzipped: Boolean): Future[WriteResult] = {

    val request = HttpRequest(
      uri = writeToInfluxQuery(
        dbName,
        consistency,
        precision,
        retentionPolicy
      ),
      method = HttpMethods.POST,
      headers = if (gzipped) AkkaHeaders.gzipEncoding :: Nil else Nil,
      entity = entity
    )

    execute(request).flatMap(toResult)
  }

  private[chronicler] override def writeFromFile(dbName: String,
                                                 filePath: String,
                                                 consistency: Consistency,
                                                 precision: Precision,
                                                 retentionPolicy: Option[String],
                                                 gzipped: Boolean): Future[WriteResult] = {

    val request = HttpRequest(
      uri = writeToInfluxQuery(
        dbName,
        consistency,
        precision,
        retentionPolicy
      ),
      method = HttpMethods.POST,
      headers = if (gzipped) AkkaHeaders.gzipEncoding :: Nil else Nil,
      entity = HttpEntity(MediaTypes.`application/octet-stream`, FileIO.fromPath(Paths.get(filePath), 1024))
    )

    execute(request).flatMap(toResult)
  }
}

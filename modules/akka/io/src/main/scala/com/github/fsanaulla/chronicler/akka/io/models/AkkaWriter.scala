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

package com.github.fsanaulla.chronicler.akka.io.models

import java.io.File

import akka.http.scaladsl.model._
import akka.stream.scaladsl.FileIO
import com.github.fsanaulla.chronicler.akka.io.headers._
import com.github.fsanaulla.chronicler.akka.shared.handlers.{AkkaQueryBuilder, AkkaRequestExecutor, AkkaResponseHandler}
import com.github.fsanaulla.chronicler.core.enums.{Consistency, Precision}
import com.github.fsanaulla.chronicler.core.io.WriteOperations
import com.github.fsanaulla.chronicler.core.model.WriteResult
import com.github.fsanaulla.chronicler.core.query.DatabaseOperationQuery

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 03.09.17
  */
private[akka] class AkkaWriter(implicit qb: AkkaQueryBuilder,
                               re: AkkaRequestExecutor,
                               rh: AkkaResponseHandler,
                               ec: ExecutionContext)
  extends DatabaseOperationQuery[Uri] with WriteOperations[Future, RequestEntity]{

  private[chronicler] override def writeTo(dbName: String,
                                           entity: RequestEntity,
                                           consistency: Option[Consistency],
                                           precision: Option[Precision],
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
      headers = if (gzipped) gzipEncoding :: Nil else Nil,
      entity = entity
    )

    re.execute(request).flatMap(rh.toResult)
  }

  private[chronicler] override def writeFromFile(dbName: String,
                                                 file: File,
                                                 consistency: Option[Consistency],
                                                 precision: Option[Precision],
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
      headers = if (gzipped) gzipEncoding :: Nil else Nil,
      entity = HttpEntity(MediaTypes.`application/octet-stream`, FileIO.fromPath(file.toPath, 1024))
    )

    re.execute(request).flatMap(rh.toResult)
  }
}

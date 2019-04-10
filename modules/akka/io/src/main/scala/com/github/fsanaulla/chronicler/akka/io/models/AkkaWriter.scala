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

import com.github.fsanaulla.chronicler.akka.shared.formats._
import com.github.fsanaulla.chronicler.akka.shared.handlers.{AkkaQueryBuilder, AkkaRequestExecutor, AkkaResponseHandler}
import com.github.fsanaulla.chronicler.core.encoding.gzipEncoding
import com.github.fsanaulla.chronicler.core.enums.{Consistency, Precision}
import com.github.fsanaulla.chronicler.core.io.WriteOperations
import com.github.fsanaulla.chronicler.core.model.WriteResult
import com.github.fsanaulla.chronicler.core.query.DatabaseOperationQuery
import com.softwaremill.sttp.{Uri, sttp}

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 03.09.17
  */
private[akka] class AkkaWriter(implicit qb: AkkaQueryBuilder,
                               re: AkkaRequestExecutor,
                               rh: AkkaResponseHandler,
                               ec: ExecutionContext)
  extends DatabaseOperationQuery[Uri] with WriteOperations[Future, String]{

  override def writeTo(dbName: String,
                       entity: String,
                       consistency: Option[Consistency],
                       precision: Option[Precision],
                       retentionPolicy: Option[String],
                       gzipped: Boolean): Future[WriteResult] = {

    val uri = writeToInfluxQuery(dbName, consistency, precision, retentionPolicy)
    val req = sttp
      .post(uri)
      .body(entity)
      .response(asJson)
    val maybeEncoded = if (gzipped) req.acceptEncoding(gzipEncoding) else req

    re.executeRequest(maybeEncoded).flatMap(rh.toWriteResult)
  }

  override def writeFromFile(dbName: String,
                             file: File,
                             consistency: Option[Consistency],
                             precision: Option[Precision],
                             retentionPolicy: Option[String],
                             gzipped: Boolean): Future[WriteResult] = {

    val uri = writeToInfluxQuery(dbName, consistency, precision, retentionPolicy)
    val req = sttp
      .post(uri)
      .body(Source.fromFile(file).getLines().mkString("\n"))
      .response(asJson)
    val maybeEncoded = if (gzipped) req.acceptEncoding(gzipEncoding) else req

    re.executeRequest(maybeEncoded).flatMap(rh.toWriteResult)
  }
}

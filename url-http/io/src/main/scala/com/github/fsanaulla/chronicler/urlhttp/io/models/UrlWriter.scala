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

package com.github.fsanaulla.chronicler.urlhttp.io.models

import com.github.fsanaulla.chronicler.core.enums.{Consistency, Precision}
import com.github.fsanaulla.chronicler.core.io.WriteOperations
import com.github.fsanaulla.chronicler.core.model.{HasCredentials, PointTransformer, WriteResult}
import com.github.fsanaulla.chronicler.core.query.DatabaseOperationQuery
import com.github.fsanaulla.chronicler.core.utils.Encodings
import com.github.fsanaulla.chronicler.urlhttp.shared.formats.asJson
import com.github.fsanaulla.chronicler.urlhttp.shared.handlers.{UrlQueryBuilder, UrlRequestExecutor, UrlResponseHandler}
import com.softwaremill.sttp._

import scala.io.Source
import scala.util.Try

private[urlhttp] trait UrlWriter
  extends DatabaseOperationQuery[Uri]
    with UrlRequestExecutor
    with UrlResponseHandler
    with UrlQueryBuilder
    with PointTransformer
    with HasCredentials
    with WriteOperations[Try, String] {

  private[chronicler] override def writeTo(dbName: String,
                                           entity: String,
                                           consistency: Consistency,
                                           precision: Precision,
                                           retentionPolicy: Option[String],
                                           gzipped: Boolean): Try[WriteResult] = {
    val uri = writeToInfluxQuery(dbName, consistency, precision, retentionPolicy)
    val req = sttp.post(uri).body(entity).response(asJson)
    val maybeEncoded = if (gzipped) req.acceptEncoding(Encodings.gzipEncoding) else req

    execute(maybeEncoded).flatMap(toResult)
  }

  private[chronicler] override def writeFromFile(dbName: String,
                                              filePath: String,
                                              consistency: Consistency,
                                              precision: Precision,
                                              retentionPolicy: Option[String],
                                              gzipped: Boolean): Try[WriteResult] = {
    val uri = writeToInfluxQuery(dbName, consistency, precision, retentionPolicy)
    val req = sttp.post(uri).body(Source.fromFile(filePath).getLines().mkString("\n")).response(asJson)
    val maybeEncoded = if (gzipped) req.acceptEncoding(Encodings.gzipEncoding) else req

    execute(maybeEncoded).flatMap(toResult)
  }

}

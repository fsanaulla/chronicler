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

package com.github.fsanaulla.chronicler.core.api

import java.io.File

import com.github.fsanaulla.chronicler.core.alias.{ErrorOr, ResponseCode}
import com.github.fsanaulla.chronicler.core.enums._
import com.github.fsanaulla.chronicler.core.model._
import com.github.fsanaulla.chronicler.core.query.DatabaseOperationQuery
import com.github.fsanaulla.chronicler.core.typeclasses._
import jawn.ast.JArray

/**
  * Generic interface for basic database IO operation
  * @tparam F - container type
  * @tparam Body - Entity type
  */
final class DatabaseApi[F[_], Req, Resp, Uri, Body](dbName: String,
                                                    gzipped: Boolean)
                                                   (implicit qb: QueryBuilder[Uri],
                                                    bd: BodyBuilder[Body],
                                                    re: RequestExecutor[F, Req, Resp, Uri, Body],
                                                    rh: ResponseHandler[Resp],
                                                    F: Functor[F]) extends DatabaseOperationQuery[Uri] {

   def writeFromFile(file: File,
                     consistency: Option[Consistency] = None,
                     precision: Option[Precision] = None,
                     retentionPolicy: Option[String]= None): F[Either[Throwable, ResponseCode]] = {
    val uri = writeToInfluxQuery(dbName, consistency, precision, retentionPolicy)
    F.map(re.execute(uri, bd.fromFile(file), gzipped))(rh.writeResult)
  }


   def writeNative(point: String,
                   consistency: Option[Consistency] = None,
                   precision: Option[Precision] = None,
                   retentionPolicy: Option[String]= None): F[Either[Throwable, ResponseCode]] = {
    val uri = writeToInfluxQuery(dbName, consistency, precision, retentionPolicy)
    F.map(re.execute(uri, bd.fromString(point), gzipped))(rh.writeResult)
  }


   def bulkWriteNative(points: Seq[String],
                       consistency: Option[Consistency] = None,
                       precision: Option[Precision] = None,
                       retentionPolicy: Option[String]= None): F[Either[Throwable, ResponseCode]] = {
    val uri = writeToInfluxQuery(dbName, consistency, precision, retentionPolicy)
    F.map(re.execute(uri, bd.fromStrings(points), gzipped))(rh.writeResult)
  }


   def writePoint(point: Point,
                  consistency: Option[Consistency] = None,
                  precision: Option[Precision] = None,
                  retentionPolicy: Option[String]= None): F[Either[Throwable, ResponseCode]] = {
    val uri = writeToInfluxQuery(dbName, consistency, precision, retentionPolicy)
    F.map(re.execute(uri, bd.fromPoint(point), gzipped))(rh.writeResult)
  }


   def bulkWritePoints(points: Seq[Point],
                       consistency: Option[Consistency] = None,
                       precision: Option[Precision] = None,
                       retentionPolicy: Option[String]= None): F[Either[Throwable, ResponseCode]] = {
    val uri = writeToInfluxQuery(dbName, consistency, precision, retentionPolicy)
    F.map(re.execute(uri, bd.fromPoints(points), gzipped))(rh.writeResult)
  }


   def readJson(query: String,
                epoch: Option[Epoch] = None,
                pretty: Boolean = false,
                chunked: Boolean = false): F[ErrorOr[Array[JArray]]] = {
    val uri = readFromInfluxSingleQuery(dbName, query, epoch, pretty, chunked)
    F.map(re.executeUri(uri))(rh.toQueryJsResult)
  }

   def bulkReadJson(queries: Seq[String],
                    epoch: Option[Epoch] = None,
                    pretty: Boolean = false,
                    chunked: Boolean = false): F[ErrorOr[Array[Array[JArray]]]] = {
    val uri = readFromInfluxBulkQuery(dbName, queries, epoch, pretty, chunked)
    F.map(re.executeUri(uri))(rh.toBulkQueryJsResult)
  }

   def readGroupedJson(query: String,
                       epoch: Option[Epoch] = None,
                       pretty: Boolean = false,
                       chunked: Boolean = false): F[ErrorOr[Array[(Array[String], JArray)]]] = {
    val uri = readFromInfluxSingleQuery(dbName, query, epoch, pretty, chunked)
    F.map(re.executeUri(uri))(rh.toGroupedJsResult)
  }
}

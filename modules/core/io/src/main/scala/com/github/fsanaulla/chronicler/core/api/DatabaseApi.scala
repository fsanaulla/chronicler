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
import com.github.fsanaulla.chronicler.core.components._
import com.github.fsanaulla.chronicler.core.enums._
import com.github.fsanaulla.chronicler.core.model._
import com.github.fsanaulla.chronicler.core.query.DatabaseOperationQuery
import jawn.ast.JArray

/**
  * Generic interface for basic database IO operation
  * @tparam F - container type
  * @tparam Body - Entity type
  */
class DatabaseApi[F[_], Resp, Uri, Body](dbName: String,
                                               gzipped: Boolean)
                                              (implicit qb: QueryBuilder[Uri],
                                               bd: BodyBuilder[Body],
                                               re: RequestExecutor[F, Resp, Uri, Body],
                                               rh: ResponseHandler[Resp],
                                               F: Functor[F]) extends DatabaseOperationQuery[Uri] {

   def writeFromFile(file: File,
                     enc: String = "UTF-8",
                     consistency: Consistency = Consistencies.None,
                     precision: Precision = Precisions.None,
                     retentionPolicy: Option[String]= None): F[Either[Throwable, ResponseCode]] = {
    val uri = write(dbName, consistency, precision, retentionPolicy)
    F.map(re.post(uri, bd.fromFile(file, enc), gzipped))(rh.writeResult)
  }


   def writeNative(point: String,
                   consistency: Consistency = Consistencies.None,
                   precision: Precision = Precisions.None,
                   retentionPolicy: Option[String]= None): F[Either[Throwable, ResponseCode]] = {
    val uri = write(dbName, consistency, precision, retentionPolicy)
    F.map(re.post(uri, bd.fromString(point), gzipped))(rh.writeResult)
  }


   def bulkWriteNative(points: Seq[String],
                       consistency: Consistency = Consistencies.None,
                       precision: Precision = Precisions.None,
                       retentionPolicy: Option[String]= None): F[Either[Throwable, ResponseCode]] = {
    val uri = write(dbName, consistency, precision, retentionPolicy)
    F.map(re.post(uri, bd.fromStrings(points), gzipped))(rh.writeResult)
  }


   def writePoint(point: Point,
                  consistency: Consistency = Consistencies.None,
                  precision: Precision = Precisions.None,
                  retentionPolicy: Option[String]= None): F[Either[Throwable, ResponseCode]] = {
    val uri = write(dbName, consistency, precision, retentionPolicy)
    F.map(re.post(uri, bd.fromPoint(point), gzipped))(rh.writeResult)
  }


   def bulkWritePoints(points: Seq[Point],
                       consistency: Consistency = Consistencies.None,
                       precision: Precision = Precisions.None,
                       retentionPolicy: Option[String]= None): F[Either[Throwable, ResponseCode]] = {
    val uri = write(dbName, consistency, precision, retentionPolicy)
    F.map(re.post(uri, bd.fromPoints(points), gzipped))(rh.writeResult)
  }


   def readJson(query: String,
                epoch: Epoch = Epochs.None,
                pretty: Boolean = false): F[ErrorOr[Array[JArray]]] = {
    val uri = singleQuery(dbName, query, epoch, pretty)
    F.map(re.get(uri))(rh.queryResultJson)
  }

   def bulkReadJson(queries: Seq[String],
                    epoch: Epoch = Epochs.None,
                    pretty: Boolean = false): F[ErrorOr[Array[Array[JArray]]]] = {
    val uri = bulkQuery(dbName, queries, epoch, pretty)
    F.map(re.get(uri))(rh.bulkQueryResultJson)
  }

   def readGroupedJson(query: String,
                       epoch: Epoch = Epochs.None,
                       pretty: Boolean = false): F[ErrorOr[Array[(Array[String], JArray)]]] = {
    val uri = singleQuery(dbName, query, epoch, pretty)
    F.map(re.get(uri))(rh.groupedResultJson)
  }
}

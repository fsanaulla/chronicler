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

package com.github.fsanaulla.chronicler.core.typeclasses

import com.github.fsanaulla.chronicler.core.alias.{ErrorOr, ResponseCode}
import com.github.fsanaulla.chronicler.core.model._
import jawn.ast.JArray

import scala.reflect.ClassTag

/**
  * This trait define response handling functionality, it's provide method's that generalize
  * response handle flow, for every backend implementation
  *
  * @tparam R - Backend HTTP response type, for example for Akka HTTP backend - HttpResponse
  */
trait ResponseHandler[R] {

  def toPingResult(response: R): ErrorOr[InfluxDBInfo]

  /**
    * Method for handling HTTP responses with empty body
    *
    * @param response - backend response value
    * @return         - Result in future container
    */
  def toWriteResult(response: R): ErrorOr[ResponseCode]

  /**
    * Method for handling HTTP responses with body, with on fly deserialization into JArray value
    *
    * @param response - backend response value
    * @return         - Query result of JArray in future container
    */
  def toQueryJsResult(response: R): ErrorOr[Array[JArray]]

  /**
    * Handling HTTP response with GROUP BY clause in the query
    *
    * @param response - backend response
    * @return         - grouped result
    */
  def toGroupedJsResult(response: R): ErrorOr[Array[(Array[String], JArray)]]

  /**
    * Method for handling HTtp responses with non empty body, that contains multiple response.
    *
    * deserialize to Seq[JArray]
    * @param response - backend response value
    * @return         - Query result with multiple response values
    */
  def toBulkQueryJsResult(response: R): ErrorOr[Array[Array[JArray]]]

  /**
    * Method for handling Info based HTTP responses, with possibility for future deserialization.
    *
    * @param response - backend response value
    * @param f        - function that transform into value of type [B]
    * @param reader   - influx reader
    * @tparam A       - entity for creating full Info object
    * @tparam B       - info object
    * @return         - Query result of [B] in future container
    */
  def toComplexQueryResult[A: ClassTag: InfluxReader, B: ClassTag](response: R,
                                                                   f: (String, Array[A]) => B): ErrorOr[Array[B]]

  /**
    * Extract HTTP response body, and transform it to A
    *
    * @param response backend response
    * @tparam A - Deserializer entity type
    * @return - Query result in future container
    */
  def toQueryResult[A: ClassTag: InfluxReader](response: R): ErrorOr[Array[A]]

  /***
    * Handler error codes by it's value
    *
    * @param code     - error code
    * @param response - response for extracting error message
    * @return         - InfluxException wrraped in container type
    */
  def errorHandler(response: R, code: Int): Throwable

  /***
    * Get CQ information from Response
    *
    * @param response - Response object
    * @param reader - implicit influx reader, predefined
    * @return - CQ results
    */
  final def toCqQueryResult(response: R)
                           (implicit reader: InfluxReader[ContinuousQuery]): ErrorOr[Array[ContinuousQueryInfo]] = {
    toComplexQueryResult[ContinuousQuery, ContinuousQueryInfo](
      response,
      (dbName: String, queries: Array[ContinuousQuery]) => ContinuousQueryInfo(dbName, queries)
    )
  }

  /***
    * Get Shard info information from Response
    *
    * @param response - Response object
    * @param reader - implicit influx reader, predefined
    * @return - Shard info  results
    */
  final def toShardQueryResult(response: R)
                              (implicit reader: InfluxReader[Shard]): ErrorOr[Array[ShardInfo]] = {
    toComplexQueryResult[Shard, ShardInfo](
      response,
      (dbName: String, shards: Array[Shard]) => ShardInfo(dbName, shards)
    )
  }

  /***
    * Get Subscription info information from Response
    *
    * @param response - Response object
    * @param reader - implicit influx reader, predefined
    * @return - Subscription info  results
    */
  final def toSubscriptionQueryResult(response: R)
                                     (implicit reader: InfluxReader[Subscription]): ErrorOr[Array[SubscriptionInfo]] = {
    toComplexQueryResult[Subscription, SubscriptionInfo](
      response,
      (dbName: String, subscriptions: Array[Subscription]) => SubscriptionInfo(dbName, subscriptions)
    )
  }

  /***
    * Get Shard group info information from Response
    *
    * @param response - Response object
    * @param reader - implicit influx reader, predefined
    * @return - Shard group info  results
    */
  final def toShardGroupQueryResult(response: R)
                                   (implicit reader: InfluxReader[ShardGroup]): ErrorOr[Array[ShardGroupsInfo]] = {
    toComplexQueryResult[ShardGroup, ShardGroupsInfo](
      response,
      (dbName: String, shardGroups: Array[ShardGroup]) => ShardGroupsInfo(dbName, shardGroups)
    )
  }

  /***
    * Check response for success
    *
    * @param code - response code
    * @return     - is it success
    */
  final def isSuccessful(code: Int): Boolean = code >= 200 && code < 300

  final def isPingCode(code: Int): Boolean = code == 200 || code == 204
}

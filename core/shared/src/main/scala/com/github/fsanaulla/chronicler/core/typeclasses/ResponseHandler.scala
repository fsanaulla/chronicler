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

package com.github.fsanaulla.chronicler.core.typeclasses

import com.github.fsanaulla.chronicler.core.model._
import jawn.ast.JArray

import scala.reflect.ClassTag

/**
  * This trait define response handling functionality, it's provide method's that generalize
  * response handle flow, for every backend implementation
  *
  * @tparam F - Container for result values.
  * @tparam R - Backend HTTP response type, for example for Akka HTTP backend - HttpResponse
  */
private[chronicler] trait ResponseHandler[F[_], R] {

  /**
    * Method for handling HTTP responses with empty body
    *
    * @param response - backend response value
    * @return         - Result in future container
    */
  private[chronicler] def toResult(response: R): F[WriteResult]

  /**
    * Method for handling HTTP responses with body, with on fly deserialization into JArray value
    *
    * @param response - backend response value
    * @return         - Query result of JArray in future container
    */
  private[chronicler] def toQueryJsResult(response: R): F[QueryResult[JArray]]

  /**
    * Handling HTTP response with GROUP BY clause in the query
    *
    * @param response - backend response
    * @return         - grouped result
    */
  private[chronicler] def toGroupedJsResult(response: R): F[GroupedResult[JArray]]

  /**
    * Method for handling HTtp responses with non empty body, that contains multiple response.
    *
    * deserialize to Seq[JArray]
    * @param response - backend response value
    * @return         - Query result with multiple response values
    */
  private[chronicler] def toBulkQueryJsResult(response: R): F[QueryResult[Array[JArray]]]

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
  private[chronicler] def toComplexQueryResult[A: ClassTag, B: ClassTag](response: R,
                                                                         f: (String, Array[A]) => B)
                                                                        (implicit reader: InfluxReader[A]): F[QueryResult[B]]

  /**
    * Extract HTTP response body, and transform it to A
    *
    * @param response backend response
    * @param reader - influx reader
    * @tparam A - Deserializer entity type
    * @return - Query result in future container
    */
  private[chronicler] def toQueryResult[A: ClassTag](response: R)(implicit reader: InfluxReader[A]): F[QueryResult[A]]

  /***
    * Handler error codes by it's value
    *
    * @param code     - error code
    * @param response - response for extracting error message
    * @return         - InfluxException wrraped in container type
    */
  private[chronicler] def errorHandler(response: R, code: Int): F[InfluxException]

  /***
    * Get CQ information from Response
    *
    * @param response - Response object
    * @param reader - implicit influx reader, predefined
    * @return - CQ results
    */
  private[chronicler] final def toCqQueryResult(response: R)(implicit reader: InfluxReader[ContinuousQuery]): F[QueryResult[ContinuousQueryInfo]] = {
    toComplexQueryResult[ContinuousQuery, ContinuousQueryInfo](
      response,
      (name: String, arr: Array[ContinuousQuery]) => ContinuousQueryInfo(name, arr)
    )
  }

  /***
    * Get Shard info information from Response
    *
    * @param response - Response object
    * @param reader - implicit influx reader, predefined
    * @return - Shard info  results
    */
  private[chronicler] final def toShardQueryResult(response: R)(implicit reader: InfluxReader[Shard]): F[QueryResult[ShardInfo]] = {
    toComplexQueryResult[Shard, ShardInfo](
      response,
      (name: String, arr: Array[Shard]) => ShardInfo(name, arr)
    )
  }

  /***
    * Get Subscription info information from Response
    *
    * @param response - Response object
    * @param reader - implicit influx reader, predefined
    * @return - Subscription info  results
    */
  private[chronicler] final def toSubscriptionQueryResult(response: R)(implicit reader: InfluxReader[Subscription]): F[QueryResult[SubscriptionInfo]] = {
    toComplexQueryResult[Subscription, SubscriptionInfo](
      response,
      (name: String, arr: Array[Subscription]) => SubscriptionInfo(name, arr)
    )
  }

  /***
    * Get Shard group info information from Response
    *
    * @param response - Response object
    * @param reader - implicit influx reader, predefined
    * @return - Shard group info  results
    */
  private[chronicler] final def toShardGroupQueryResult(response: R)(implicit reader: InfluxReader[ShardGroup]): F[QueryResult[ShardGroupsInfo]] = {
    toComplexQueryResult[ShardGroup, ShardGroupsInfo](
      response,
      (name: String, arr: Array[ShardGroup]) => ShardGroupsInfo(name, arr)
    )
  }

  /***
    * Check response for success
    *
    * @param code - response code
    * @return - is it success
    */
  private[chronicler] final def isSuccessful(code: Int): Boolean = code >= 200 && code < 300
}

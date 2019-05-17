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

package com.github.fsanaulla.chronicler.core.components

import com.github.fsanaulla.chronicler.core.alias.{ErrorOr, ResponseCode}
import com.github.fsanaulla.chronicler.core.either
import com.github.fsanaulla.chronicler.core.either._
import com.github.fsanaulla.chronicler.core.model._
import jawn.ast.JArray

import scala.reflect.ClassTag

/**
  * Response handling functionality, it's provide method's that generalize
  * response handle flow, for every backend implementation
  *
  * @tparam R - Backend HTTP response type, for example for Akka HTTP backend - HttpResponse
  */
final class ResponseHandler[R](jsonHandler: JsonHandler[R]) {

  def pingResult(response: R): ErrorOr[InfluxDBInfo] = {
    if (isPingCode(jsonHandler.responseCode(response))) jsonHandler.databaseInfo(response)
    else Left(errorHandler(response))
  }

  /**
    * Method for handling HTTP responses with empty body
    *
    * @param response - backend response value
    * @return         - Result in future container
    */
  def writeResult(response: R): ErrorOr[ResponseCode] = {
    jsonHandler.responseCode(response) match {
      case code if isSuccessful(code) && code != 204 =>
        jsonHandler
          .responseErrorMsgOpt(response)
          .flatMapRight(_.fold[ErrorOr[ResponseCode]](Right(code))(str => Left(InfluxException(code, str))))
      case 204 =>
        Right(204)
      case _ =>
        Left(errorHandler(response))
    }
  }

  /**
    * Method for handling HTTP responses with body, with on fly deserialization into JArray value
    *
    * @param response - backend response value
    * @return         - Query result of JArray in future container
    */
  def queryResultJson(response: R): ErrorOr[Array[JArray]] = {
    jsonHandler.responseCode(response).intValue() match {
      case code if isSuccessful(code) =>
        jsonHandler
          .responseBody(response)
          .flatMapRight(jsonHandler.queryResult)
      case _ =>
        Left(errorHandler(response))
    }
  }

  /**
    * Handling HTTP response with GROUP BY clause in the query
    *
    * @param response - backend response
    * @return         - grouped result
    */
  def groupedResultJson(response: R): ErrorOr[Array[(Array[String], JArray)]] =
    jsonHandler.responseCode(response) match {
      case code if isSuccessful(code) =>
        jsonHandler
          .responseBody(response)
          .flatMapRight(jsonHandler.gropedResult)
      case _ =>
        Left(errorHandler(response))
    }

  /**
    * Method for handling HTtp responses with non empty body, that contains multiple response.
    *
    * deserialize to Seq[JArray]
    * @param response - backend response value
    * @return         - Query result with multiple response values
    */
  def bulkQueryResultJson(response: R): ErrorOr[Array[Array[JArray]]] =
    jsonHandler.responseCode(response) match {
      case code if isSuccessful(code) =>
        jsonHandler
          .responseBody(response)
          .flatMapRight(jsonHandler.bulkResult)
      case _ =>
        Left(errorHandler(response))
    }

  /**
    * Method for handling Info based HTTP responses, with possibility for future deserialization.
    *
    * @param response - backend response value
    * @param f        - function that transform into value of type [B]
    * @tparam A       - entity for creating full Info object
    * @tparam B       - info object
    * @return         - Query result of [B] in future container
    */
  def toComplexQueryResult[A: ClassTag: InfluxReader, B: ClassTag](response: R,
                                                                   f: (String, Array[A]) => B): ErrorOr[Array[B]] = {
      jsonHandler.responseCode(response) match {
        case code if isSuccessful(code) =>
          jsonHandler.responseBody(response)
            .flatMapRight(jsonHandler.groupedSystemInfo[A])
            .mapRight(_.map { case (dbName, queries) => f(dbName, queries) })
        case _ =>
          Left(errorHandler(response))
      }
  }

  /**
    * Extract HTTP response body, and transform it to A
    *
    * @param response backend response
    * @tparam A - Deserializer entity type
    * @return - Query result in future container
    */
  def queryResust[A: ClassTag](response: R)(implicit rd: InfluxReader[A]): ErrorOr[Array[A]] =
    queryResultJson(response)
      .mapRight(_.map(rd.read))
      .mapRight(either.array)
      .joinRight


  /***
    * Handler error codes by it's value
    *
    * @param response - response for extracting error message
    * @return         - InfluxException wrraped in container type
    */
  def errorHandler(response: R): Throwable =
    jsonHandler
      .responseErrorMsg(response)
      .mapRight(InfluxException(jsonHandler.responseCode(response), _))
      // merging parsing level issues with request level issues
      .merge

  /***
    * Get CQ information from Response
    *
    * @param response - Response object
    * @param reader - implicit influx reader, predefined
    * @return - CQ results
    */
  def toCqQueryResult(response: R)
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
  def toShardQueryResult(response: R)
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
  def toSubscriptionQueryResult(response: R)
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
  def toShardGroupQueryResult(response: R)
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
  def isSuccessful(code: Int): Boolean = code >= 200 && code < 300

  def isPingCode(code: Int): Boolean = code == 200 || code == 204
}

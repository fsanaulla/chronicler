package com.github.fsanaulla.chronicler.core.handlers

import com.github.fsanaulla.chronicler.core.model._
import jawn.ast.JArray

import scala.reflect.ClassTag

/**
  * This trait define response handling functionality, it's provide method's that generalize
  * response handle flow, for every backend implementation
  * @tparam M - Container for result values.
  * @tparam R - Backend HTTP response type, for example for Akka HTTP backend - HttpResponse
  */
private[chronicler] trait ResponseHandler[M[_], R] {

  /**
    * Method for handling HTTP responses with empty body
    * @param response - backend response value
    * @return         - Result in future container
    */
  def toResult(response: R): M[WriteResult]

  /**
    * Method for handling HTTP responses with body, with on fly deserialization into JArray value
    * @param response - backaend response value
    * @return         - Query result of JArray in future container
    */
  def toQueryJsResult(response: R): M[QueryResult[JArray]]

  /**
    * Handling HTTP response with GROUP BY clause in the query
    * @param response - backand response
    * @return         - grouped result
    */
  def toGroupedJsResult(response: R): M[GroupedResult[JArray]]

  /**
    * Method for handling HTtp responses with non empty body, that contains multiple response.
    * deserialized into Seq[JArray]
    * @param response - backend response value
    * @return         - Query result with multiple response values
    */
  def toBulkQueryJsResult(response: R): M[QueryResult[Array[JArray]]]

  /**
    * Method for handling Info based HTTP responses, with possibility for future deserialization.
    * @param response - backend response value
    * @param f        - function that transform into value of type [B]
    * @param reader   - influx reader
    * @tparam A       - entity for creating full Info object
    * @tparam B       - info object
    * @return         - Query result of [B] in future container
    */
  def toComplexQueryResult[A: ClassTag, B: ClassTag](response: R,
                                                     f: (String, Array[A]) => B)
                                                    (implicit reader: InfluxReader[A]): M[QueryResult[B]]

  /**
    * Extract HTTP response body, and transform it to A
    * @param response backend response
    * @param reader - influx reader
    * @tparam A - Deserializer entity type
    * @return - Query result in future container
    */
  def toQueryResult[A: ClassTag](response: R)(implicit reader: InfluxReader[A]): M[QueryResult[A]]

  /***
    * Handler error codes by it's value
    * @param code     - error code
    * @param response - response for extracting error message
    * @return         - InfluxException wrraped in container type
    */
  def errorHandler(response: R, code: Int): M[InfluxException]

  /***
    * Get CQ information from Response
    * @param response - Response object
    * @param reader - implicit influx reader, predefined
    * @return - CQ results
    */
  final def toCqQueryResult(response: R)(implicit reader: InfluxReader[ContinuousQuery]): M[QueryResult[ContinuousQueryInfo]] = {
    toComplexQueryResult[ContinuousQuery, ContinuousQueryInfo](
      response,
      (name: String, arr: Array[ContinuousQuery]) => ContinuousQueryInfo(name, arr)
    )
  }

  /***
    * Get Shard info information from Response
    * @param response - Response object
    * @param reader - implicit influx reader, predefined
    * @return - Shard info  results
    */
  final def toShardQueryResult(response: R)(implicit reader: InfluxReader[Shard]): M[QueryResult[ShardInfo]] = {
    toComplexQueryResult[Shard, ShardInfo](
      response,
      (name: String, arr: Array[Shard]) => ShardInfo(name, arr)
    )
  }

  /***
    * Get Subscription info information from Response
    * @param response - Response object
    * @param reader - implicit influx reader, predefined
    * @return - Subscription info  results
    */
  final def toSubscriptionQueryResult(response: R)(implicit reader: InfluxReader[Subscription]): M[QueryResult[SubscriptionInfo]] = {
    toComplexQueryResult[Subscription, SubscriptionInfo](
      response,
      (name: String, arr: Array[Subscription]) => SubscriptionInfo(name, arr)
    )
  }

  /***
    * Get Shard group info information from Response
    * @param response - Response object
    * @param reader - implicit influx reader, predefined
    * @return - Shard group info  results
    */
  final def toShardGroupQueryResult(response: R)(implicit reader: InfluxReader[ShardGroup]): M[QueryResult[ShardGroupsInfo]] = {
    toComplexQueryResult[ShardGroup, ShardGroupsInfo](
      response,
      (name: String, arr: Array[ShardGroup]) => ShardGroupsInfo(name, arr)
    )
  }

  /***
    * Check response for success
    * @param code - response code
    * @return - is it success
    */
  final def isSuccessful(code: Int): Boolean = code >= 200 && code < 300
}

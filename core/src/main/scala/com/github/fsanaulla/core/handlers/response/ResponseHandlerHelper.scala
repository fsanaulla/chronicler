package com.github.fsanaulla.core.handlers.response

import com.github.fsanaulla.core.model._
import com.github.fsanaulla.core.utils.DefaultInfluxImplicits._

import scala.concurrent.Future
import scala.reflect.ClassTag

/***
  * Predefined methods for response handling from response: R
  */
private[core] trait ResponseHandlerHelper[R] {
  self: ResponseHandler[R] with Executable =>

  /**
  * Extract HTTP response body, and transform it to A
  * @param response backend response
  * @param reader - influx reader
  * @tparam A - Deserializer entity type
  * @return - Query result in future container
  */
  def toQueryResult[A: ClassTag](response: R)(implicit reader: InfluxReader[A]): Future[QueryResult[A]] =
    toQueryJsResult(response).map(_.transform(reader.read))

  /***
    * Get CQ information from Response
    * @param response - Response object
    * @param reader - implicit influx reader, predefined
    * @return - CQ results
    */
  def toCqQueryResult(response: R)(implicit reader: InfluxReader[ContinuousQuery]): Future[QueryResult[ContinuousQueryInfo]] = {
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
  def toShardQueryResult(response: R)(implicit reader: InfluxReader[Shard]): Future[QueryResult[ShardInfo]] = {
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
  def toSubscriptionQueryResult(response: R)(implicit reader: InfluxReader[Subscription]): Future[QueryResult[SubscriptionInfo]] = {
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
  def toShardGroupQueryResult(response: R)(implicit reader: InfluxReader[ShardGroup]): Future[QueryResult[ShardGroupsInfo]] = {
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
  def isSuccessful(code: Int): Boolean = code >= 200 && code < 300

  /***
    * Handler error codes by it's value
    * @param code - error code
    * @param response - response for extracting error message
    * @return - InfluxException
    */
  def errorHandler(code: Int, response: R): Future[InfluxException] = code match {
    case 400 =>
      getError(response).map(errMsg => new BadRequestException(errMsg))
    case 401 =>
      getError(response).map(errMsg => new AuthorizationException(errMsg))
    case 404 =>
      getError(response).map(errMsg => new ResourceNotFoundException(errMsg))
    case code: Int if code < 599 && code >= 500 =>
      getError(response).map(errMsg => new InternalServerError(errMsg))
    case _ =>
      getError(response).map(errMsg => new UnknownResponseException(errMsg))
  }
}

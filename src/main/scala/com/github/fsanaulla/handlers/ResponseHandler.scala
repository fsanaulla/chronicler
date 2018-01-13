package com.github.fsanaulla.handlers

import com.github.fsanaulla.model.InfluxImplicits._
import com.github.fsanaulla.model._
import spray.json.JsArray

import scala.concurrent.{ExecutionContext, Future}

/**
  * This trait define response handling functionality, it's provide method's that generalize
  * response handle flow, for every backend implementation
  * @tparam R - Backend HTTP response type, for example for Akka HTTP backend - HttpResponse
  */
trait ResponseHandler[R] {

  protected implicit val ex: ExecutionContext

  /**
    * Method for handling HTTP responses with empty body
    * @param response - backend response value
    * @return - Result in future container
    */
  def toResult(response: R): Future[Result]

  /**
    * Method for handling HTTP responses with body, with on fly deserialization into JsArray value
    * @param response - backaend response value
    * @return - Query result of JsArray in future container
    */
  def toQueryJsResult(response: R): Future[QueryResult[JsArray]]

  /**
    * Method for handling HTtp responses with non empty body, that contains multiple response.
    * deserialized into Seq[JsArray]
    * @param response - backend response value
    * @return - Query result with multiple response values
    */
  def toBulkQueryJsResult(response: R): Future[QueryResult[Seq[JsArray]]]

  /**
    * Method for handling Info based HTTP responses, with possibility for future deserialization.
    * @param response - backend response value
    * @param f - function that transform into value of type [B]
    * @param reader - influx reader
    * @tparam A - entity for creating full Info object
    * @tparam B - info object
    * @return - Query result of [B] in future container
    */
  def toComplexQueryResult[A, B](response: R, f: (String, Seq[A]) => B)(implicit reader: InfluxReader[A]): Future[QueryResult[B]]

  /**
    * Method for handling HTTP responses with body
    * @param response backend response
    * @param reader - influx reader, for deserializing
    * @tparam A - Deserialized entity type
    * @return - Query result in future container
    */
  def toQueryResult[A](response: R)(implicit reader: InfluxReader[A]): Future[QueryResult[A]] = {
    toQueryJsResult(response)
      .map(
        res =>
          QueryResult[A](
            res.code,
            isSuccess = res.isSuccess,
            res.queryResult.map(reader.read),
            res.ex
          ))
  }

  def toCqQueryResult(response: R)(implicit reader: InfluxReader[ContinuousQuery]): Future[QueryResult[ContinuousQueryInfo]] = {
    toComplexQueryResult[ContinuousQuery, ContinuousQueryInfo](
      response,
      (name: String, seq: Seq[ContinuousQuery]) => ContinuousQueryInfo(name, seq)
    )
  }

  def toShardQueryResult(response: R)(implicit reader: InfluxReader[Shard]): Future[QueryResult[ShardInfo]] = {
    toComplexQueryResult[Shard, ShardInfo](
      response,
      (name: String, seq: Seq[Shard]) => ShardInfo(name, seq)
    )
  }

  def toSubscriptionQueryResult(response: R)(implicit reader: InfluxReader[Subscription]): Future[QueryResult[SubscriptionInfo]] = {
    toComplexQueryResult[Subscription, SubscriptionInfo](
      response,
      (name: String, seq: Seq[Subscription]) => SubscriptionInfo(name, seq)
    )
  }

  def toShardGroupQueryResult(response: R)(implicit reader: InfluxReader[ShardGroup]): Future[QueryResult[ShardGroupsInfo]] = {
    toComplexQueryResult[ShardGroup, ShardGroupsInfo](
      response,
      (name: String, seq: Seq[ShardGroup]) => ShardGroupsInfo(name, seq)
    )
  }
}

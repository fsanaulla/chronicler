package com.github.fsanaulla.chronicler.core.management

import com.github.fsanaulla.chronicler.core.components.{ResponseHandlerBase, JsonHandler}
import com.github.fsanaulla.chronicler.core.typeclasses.{Functor, Apply}
import com.github.fsanaulla.chronicler.core.management.cq._
import com.github.fsanaulla.chronicler.core.management.shard._
import com.github.fsanaulla.chronicler.core.management.subscription._
import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.model.{InfluxReader, InfluxException}
import com.github.fsanaulla.chronicler.core.either._

import scala.reflect.ClassTag

class ManagementResponseHandler[G[_], R](
    jsonHandler: JsonHandler[G, R]
)(implicit F: Functor[G], A: Apply[G])
    extends ResponseHandlerBase[G, R](jsonHandler) {

  /**
    * Method for handling Info based HTTP responses, with possibility for future deserialization.
    *
    * @param response - backend response value
    * @param f        - function that transform into value of type [B]
    * @tparam A       - entity for creating full Info object
    * @tparam B       - info object
    * @return         - Query result of [B] in future container
    */
  final def toComplexQueryResult[A: ClassTag: InfluxReader, B: ClassTag](
      response: R,
      f: (String, Array[A]) => B
  ): G[ErrorOr[Array[B]]] = {
    jsonHandler.responseCode(response) match {
      case code if isSuccessful(code) =>
        F.map(jsonHandler.responseBody(response)) { body =>
          body
            .flatMapRight(jsonHandler.groupedSystemInfo[A])
            .mapRight { arr =>
              arr.map { case (dbName, queries) => f(dbName, queries) }
            }
        }
      case 401 =>
        A.pure(Left(new InfluxException(401, "Authorized")))
      case _ =>
        F.map(errorHandler(response))(Left(_))
    }
  }

  /***
    * Get CQ information from Response
    *
    * @param response - Response object
    * @param reader - implicit influx reader, predefined
    * @return - CQ results
    */
  def toCqQueryResult(
      response: R
  )(implicit reader: InfluxReader[ContinuousQuery]): G[ErrorOr[Array[ContinuousQueryInfo]]] = {
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
  final def toShardQueryResult(
      response: R
  )(implicit reader: InfluxReader[Shard]): G[ErrorOr[Array[ShardInfo]]] = {
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
  final def toSubscriptionQueryResult(
      response: R
  )(implicit reader: InfluxReader[Subscription]): G[ErrorOr[Array[SubscriptionInfo]]] = {
    toComplexQueryResult[Subscription, SubscriptionInfo](
      response,
      (dbName: String, subscriptions: Array[Subscription]) =>
        SubscriptionInfo(dbName, subscriptions)
    )
  }

  /***
    * Get Shard group info information from Response
    *
    * @param response - Response object
    * @param reader - implicit influx reader, predefined
    * @return - Shard group info  results
    */
  final def toShardGroupQueryResult(
      response: R
  )(implicit reader: InfluxReader[ShardGroup]): G[ErrorOr[Array[ShardGroupsInfo]]] = {
    toComplexQueryResult[ShardGroup, ShardGroupsInfo](
      response,
      (dbName: String, shardGroups: Array[ShardGroup]) => ShardGroupsInfo(dbName, shardGroups)
    )
  }

}

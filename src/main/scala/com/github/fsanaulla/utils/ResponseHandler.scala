package com.github.fsanaulla.utils

import akka.http.scaladsl.model.HttpResponse
import akka.stream.ActorMaterializer
import com.github.fsanaulla.model.InfluxImplicits._
import com.github.fsanaulla.model._
import com.github.fsanaulla.utils.JsonSupport._
import spray.json.{JsArray, JsObject}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 31.07.17
  */
private[fsanaulla] object ResponseHandler {

  // Simply result's
  def toResult(response: HttpResponse)(implicit ex: ExecutionContext, mat: ActorMaterializer): Future[Result] = {
    response.status.intValue() match {
      case code if isSuccessful(code) && code != 204 =>
        getErrorOpt(response) map {
          case Some(msg) =>
            Result.failed(code, new OperationException(msg))
          case _ =>
            Result.successful(code)
        }
      case 204 =>
        Result.successfulFuture(204)
      case other =>
        errorHandler(other, response).map(ex => Result.failed(other, ex))
    }
  }

  def toQueryResult[T](response: HttpResponse)(implicit ex: ExecutionContext, mat: ActorMaterializer, reader: InfluxReader[T]): Future[QueryResult[T]] = {
    toQueryJsResult(response)
      .map(
        res =>
          QueryResult[T](
            res.code,
            isSuccess = res.isSuccess,
            res.queryResult.map(reader.read),
            res.ex
        ))
  }

  // Complex query result's
  def toCqQueryResult(response: HttpResponse)(implicit ex: ExecutionContext, mat: ActorMaterializer, reader: InfluxReader[ContinuousQuery]): Future[QueryResult[ContinuousQueryInfo]] = {
    toComplexQueryResult[ContinuousQuery, ContinuousQueryInfo](
      response,
      (name: String, seq: Seq[ContinuousQuery]) => ContinuousQueryInfo(name, seq)
    )
  }

  // format: off
  def toShardQueryResult(response: HttpResponse)(implicit ex: ExecutionContext,
                                                 mat: ActorMaterializer,
                                                 reader: InfluxReader[Shard]): Future[QueryResult[ShardInfo]] = {
    toComplexQueryResult[Shard, ShardInfo](
      response,
      (name: String, seq: Seq[Shard]) => ShardInfo(name, seq)
    )
  }

  def toSubscriptionQueryResult(response: HttpResponse)(implicit ex: ExecutionContext,
                                                        mat: ActorMaterializer,
                                                        reader: InfluxReader[Shard]): Future[QueryResult[SubscriptionInfo]] = {
    toComplexQueryResult[Subscription, SubscriptionInfo](
      response,
      (name: String, seq: Seq[Subscription]) => SubscriptionInfo(name, seq)
    )
  }

  def toShardGroupQueryResult(response: HttpResponse)(implicit ex: ExecutionContext,
                                                      mat: ActorMaterializer,
                                                      reader: InfluxReader[Shard]): Future[QueryResult[ShardGroupsInfo]] = {
    toComplexQueryResult[ShardGroup, ShardGroupsInfo](
      response,
      (name: String, seq: Seq[ShardGroup]) => ShardGroupsInfo(name, seq)
    )
  }

  private def toComplexQueryResult[A, B](response: HttpResponse, f: (String, Seq[A]) => B)(implicit ex: ExecutionContext,
                                                                                           mat: ActorMaterializer,
                                                                                           reader: InfluxReader[A]): Future[QueryResult[B]] = {
    response.status.intValue() match {
      case code if isSuccessful(code) =>
        unmarshalBody(response)
          .map(getInfluxInfo[A])
          .map(seq => seq.map(e => f(e._1, e._2)))
          .map(seq => QueryResult.successful[B](code, seq))
      case other =>
        errorHandler(other, response)
          .map(ex => QueryResult.failed[B](other, ex))
    }
  }

  // QUERY RESULT
  def toQueryJsResult(response: HttpResponse)(implicit ex: ExecutionContext,
                                              mat: ActorMaterializer): Future[QueryResult[JsArray]] = {
    response.status.intValue() match {
      case code if isSuccessful(code) =>
        unmarshalBody(response)
          .map(getInfluxValue)
          .map(seq => QueryResult.successful[JsArray](code, seq))
      case other =>
        errorHandler(other, response)
          .map(ex => QueryResult.failed[JsArray](other, ex))
    }
  }

  def toBulkQueryJsResult(response: HttpResponse)(implicit ex: ExecutionContext, mat: ActorMaterializer): Future[QueryResult[Seq[JsArray]]] = {
    response.status.intValue() match {
      case code if isSuccessful(code) =>
        unmarshalBody(response)
          .map(getBulkInfluxValue)
          .map(seq => QueryResult.successful[Seq[JsArray]](code, seq))
      case other =>
        errorHandler(other, response)
          .map(ex => QueryResult.failed[Seq[JsArray]](other, ex))
    }
  }

  def getError(response: HttpResponse)(implicit ex: ExecutionContext,
                                       mat: ActorMaterializer): Future[String] = {
    unmarshalBody(response)
      .map(
        _.getFields("error").head
          .convertTo[String])
  }

  def getErrorOpt(response: HttpResponse)(implicit ex: ExecutionContext,
                                          mat: ActorMaterializer): Future[Option[String]] = {
    unmarshalBody(response)
      .map(
        _.getFields("results").head
          .convertTo[Seq[JsObject]]
          .head
          .fields
          .get("error")
          .map(_.convertTo[String]))
  }

  private def isSuccessful(code: Int): Boolean = {
    if (code >= 200 && code < 300) true else false
  }

  private def errorHandler(code: Int, response: HttpResponse)(implicit ex: ExecutionContext,
                                                              mat: ActorMaterializer): Future[InfluxException] = code match {
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

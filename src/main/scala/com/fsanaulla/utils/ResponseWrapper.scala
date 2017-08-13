package com.fsanaulla.utils

import akka.http.scaladsl.model.HttpResponse
import akka.stream.ActorMaterializer
import com.fsanaulla.model._
import com.fsanaulla.utils.JsonSupport._
import spray.json.{JsArray, JsObject}

import scala.concurrent.{ExecutionContext, Future}
/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 31.07.17
  */
private[fsanaulla] object ResponseWrapper {

  // RESULT
  def toResult(response: HttpResponse)(implicit ex: ExecutionContext, mat: ActorMaterializer): Future[Result] = {
    response.status.intValue() match {
      case code if isSuccessful(code) && code != 204 => getErrorOpt(response) map {
        case Some(msg) => Result(code, isSuccess = false, Some(new OperationException(msg)))
        case _ => Result.successful(code)
      }
      case 204  => Future.successful(Result.successful(204))
      case other => errorHandler(other, response).map(ex => Result(other, isSuccess = false, Some(ex)))
    }
  }


  // CQ QUERY RESULT
  def toCqQueryResult(response: HttpResponse)(implicit ex: ExecutionContext, mat: ActorMaterializer, reader: InfluxReader[ContinuousQuery]): Future[QueryResult[ContinuousQueryInfo]] = {
    response.status.intValue() match {
      case code if isSuccessful(code) => unmarshalBody(response).map(getInfluxCQInfo).map(seq => QueryResult[ContinuousQueryInfo](code, isSuccess = true, seq))
      case other => errorHandler(other, response).map(ex => QueryResult[ContinuousQueryInfo](other, isSuccess = false, ex = Some(ex)))
    }
  }

  // QUERY RESULT
  def toQueryJsResult(response: HttpResponse)(implicit ex: ExecutionContext, mat: ActorMaterializer): Future[QueryResult[JsArray]] = {
    response.status.intValue() match {
      case code if isSuccessful(code) => unmarshalBody(response).map(getInfluxValue).map(seq => QueryResult(code, isSuccess = true, seq))
      case other => errorHandler(other, response).map(ex => QueryResult(other, isSuccess = false, ex = Some(ex)))
    }
  }

  def toQueryResult[T](response: HttpResponse)(implicit ex: ExecutionContext, mat: ActorMaterializer, reader: InfluxReader[T]): Future[QueryResult[T]] = {
    toQueryJsResult(response).map(res => QueryResult[T](res.code, isSuccess = res.isSuccess, res.queryResult.map(reader.read)))
  }

  // BULK QUERY RESULT
  def toBulkJsResult(response: HttpResponse)(implicit ex: ExecutionContext, mat: ActorMaterializer): Future[Seq[Seq[JsArray]]] = {
    unmarshalBody(response)
      .map(_.getFields("results").head.convertTo[Seq[JsObject]])
      .map(_.map(_.getFields("series").head.convertTo[Seq[JsObject]].head))
      .map(_.map(_.getFields("values").head.convertTo[Seq[JsArray]]))
  }

  def toBulkQueryJsResult(response: HttpResponse)(implicit ex: ExecutionContext, mat: ActorMaterializer): Future[QueryResult[Seq[JsArray]]] = {
    response.status.intValue() match {
      case code if isSuccessful(code) =>
        toBulkJsResult(response).map(seq => QueryResult[Seq[JsArray]](code, isSuccess = true, seq))
      case other => errorHandler(other, response).map(ex => QueryResult(other, isSuccess = false, ex = Some(ex)))
    }
  }

  private def isSuccessful(code: Int): Boolean = if (code >= 200 && code < 300) true else false

  private def errorHandler(code: Int, response: HttpResponse)(implicit ex: ExecutionContext, mat: ActorMaterializer): Future[InfluxException] = code match {
    case 400 => getError(response).map(errMsg => new BadRequestException(errMsg))
    case 404 => getError(response).map(errMsg => new ResourceNotFoundException(errMsg))
    case code: Int if code < 599 && code >= 500 => getError(response).map(errMsg => new InternalServerError(errMsg))
    case other => getError(response).map(errMsg => new UnknownResponseException(errMsg))
  }

  def getError(response: HttpResponse)(implicit ex: ExecutionContext, mat: ActorMaterializer): Future[String] = unmarshalBody(response).map(_.getFields("error").head.convertTo[String])

  def getErrorOpt(response: HttpResponse)(implicit ex: ExecutionContext, mat: ActorMaterializer): Future[Option[String]] = {
    unmarshalBody(response).map(_.getFields("results").head.convertTo[Seq[JsObject]].head.fields.get("error").map(_.convertTo[String]))
  }
}


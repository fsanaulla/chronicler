package com.github.fsanaulla.chronicler.async.handlers

import com.github.fsanaulla.core.handlers.response.ResponseHandler
import com.github.fsanaulla.core.model._
import com.softwaremill.sttp.Response
import jawn.ast.{JArray, JValue}

import scala.concurrent.Future
import scala.reflect.ClassTag

private[fsanaulla] trait AsyncResponseHandler extends ResponseHandler[Response[JValue]] with AsyncJsonHandler {

  // Simply result's
  def toResult(response: Response[JValue]): Future[Result] = {
    response.code match {
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

  def toComplexQueryResult[A: ClassTag, B: ClassTag](
                                                      response: Response[JValue],
                                                      f: (String, Array[A]) => B)
                                                    (implicit reader: InfluxReader[A]): Future[QueryResult[B]] = {
    response.code match {
      case code if isSuccessful(code) =>
        getResponseBody(response)
          .map(getOptInfluxInfo[A])
          .map {
            case Some(arr) =>
              QueryResult.successful[B](code, arr.map(e => f(e._1, e._2)))
            case _ =>
              QueryResult.empty[B](code)
          }
      case other =>
        errorHandler(other, response)
          .map(ex => QueryResult.failed[B](other, ex))
    }
  }

  // QUERY RESULT
  def toQueryJsResult(response: Response[JValue]): Future[QueryResult[JArray]] = {
    response.code.intValue() match {
      case code if isSuccessful(code) =>
        getResponseBody(response)
          .map(getOptInfluxPoints)
          .map {
            case Some(seq) => QueryResult.successful[JArray](code, seq)
            case _ => QueryResult.empty[JArray](code)}
      case other =>
        errorHandler(other, response)
          .map(ex => QueryResult.failed[JArray](other, ex))
    }
  }

  def toBulkQueryJsResult(response: Response[JValue]): Future[QueryResult[Array[JArray]]] = {
    response.code.intValue() match {
      case code if isSuccessful(code) =>
        getResponseBody(response)
          .map(getOptBulkInfluxPoints)
          .map {
            case Some(seq) => QueryResult.successful[Array[JArray]](code, seq)
            case _ => QueryResult.empty[Array[JArray]](code)
          }
      case other =>
        errorHandler(other, response)
          .map(ex => QueryResult.failed[Array[JArray]](other, ex))
    }
  }
}

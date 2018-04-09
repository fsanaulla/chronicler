package com.github.fsanaulla.chronicler.akka.handlers

import _root_.akka.http.scaladsl.model.HttpResponse
import com.github.fsanaulla.core.handlers.response.ResponseHandler
import com.github.fsanaulla.core.model._
import jawn.ast.JArray

import scala.concurrent.Future
import scala.reflect.ClassTag

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 15.03.18
  */
private[fsanaulla] trait AkkaResponseHandler extends ResponseHandler[HttpResponse] with AkkaJsonHandler {

  // Simply result's
  def toResult(response: HttpResponse): Future[Result] = {
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

  def toComplexQueryResult[A: ClassTag, B: ClassTag](response: HttpResponse,
                                                     f: (String, Array[A]) => B)
                                                    (implicit reader: InfluxReader[A]): Future[QueryResult[B]] = {
    response.status.intValue() match {
      case code if isSuccessful(code) =>
        getJsBody(response)
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
  def toQueryJsResult(response: HttpResponse): Future[QueryResult[JArray]] = {
    response.status.intValue() match {
      case code if isSuccessful(code) =>
        getJsBody(response)
          .map(getOptInfluxPoints)
          .map {
            case Some(seq) => QueryResult.successful[JArray](code, seq)
            case _ => QueryResult.empty[JArray](code)}
      case other =>
        errorHandler(other, response)
          .map(ex => QueryResult.failed[JArray](other, ex))
    }
  }

  def toBulkQueryJsResult(response: HttpResponse): Future[QueryResult[Array[JArray]]] = {
    response.status.intValue() match {
      case code if isSuccessful(code) =>
        getJsBody(response)
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

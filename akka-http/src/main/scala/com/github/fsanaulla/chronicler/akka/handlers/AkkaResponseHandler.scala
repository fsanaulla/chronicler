package com.github.fsanaulla.chronicler.akka.handlers

import _root_.akka.http.scaladsl.model.HttpResponse
import com.github.fsanaulla.core.handlers.response.ResponseHandler
import com.github.fsanaulla.core.model._
import spray.json.{JsArray, JsObject}

import scala.concurrent.Future

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 15.03.18
  */
private[fsanaulla] trait AkkaResponseHandler
  extends ResponseHandler[HttpResponse]
    with AkkaJsonHandler
    with Executable {

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

  def toComplexQueryResult[A, B](response: HttpResponse,
                                 f: (String, Seq[A]) => B)(implicit reader: InfluxReader[A]): Future[QueryResult[B]] = {
    response.status.intValue() match {
      case code if isSuccessful(code) =>
        getJsBody(response)
          .map(getInfluxInfo[A])
          .map(seq => seq.map(e => f(e._1, e._2)))
          .map(seq => QueryResult.successful[B](code, seq))
      case other =>
        errorHandler(other, response)
          .map(ex => QueryResult.failed[B](other, ex))
    }
  }

  // QUERY RESULT
  def toQueryJsResult(response: HttpResponse): Future[QueryResult[JsArray]] = {
    response.status.intValue() match {
      case code if isSuccessful(code) =>
        getJsBody(response)
          .map(getInfluxPoints)
          .map(seq => QueryResult.successful[JsArray](code, seq))
      case other =>
        errorHandler(other, response)
          .map(ex => QueryResult.failed[JsArray](other, ex))
    }
  }

  def toBulkQueryJsResult(response: HttpResponse): Future[QueryResult[Seq[JsArray]]] = {
    response.status.intValue() match {
      case code if isSuccessful(code) =>
        getJsBody(response)
          .map(getBulkInfluxValue)
          .map(seq => QueryResult.successful[Seq[JsArray]](code, seq))
      case other =>
        errorHandler(other, response)
          .map(ex => QueryResult.failed[Seq[JsArray]](other, ex))
    }
  }

  def getError(response: HttpResponse): Future[String] = {
    getJsBody(response)
      .map(_.getFields("error").head.convertTo[String])
  }

  def getErrorOpt(response: HttpResponse): Future[Option[String]] = {
    getJsBody(response)
      .map(
        _.getFields("results")
          .headOption
          .flatMap(_.convertTo[Seq[JsObject]].headOption)
          .flatMap(_.fields.get("error"))
          .map(_.convertTo[String]))
  }
}

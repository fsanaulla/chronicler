package com.github.fsanaulla.chronicler.async.handlers

import com.github.fsanaulla.core.handlers.ResponseHandler
import com.github.fsanaulla.core.model._
import com.softwaremill.sttp.Response
import spray.json.{JsArray, JsObject}

import scala.concurrent.Future

private[fsanaulla] trait AsyncResponseHandler
  extends ResponseHandler[Response[JsObject]]
    with AsyncJsonHandler {

  // Simply result's
  def toResult(response: Response[JsObject]): Future[Result] = {
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

  def toComplexQueryResult[A, B](response: Response[JsObject],
                                 f: (String, Seq[A]) => B)
                                (implicit reader: InfluxReader[A]): Future[QueryResult[B]] = {
    response.code match {
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
  def toQueryJsResult(response: Response[JsObject]): Future[QueryResult[JsArray]] = {
    response.code.intValue() match {
      case code if isSuccessful(code) =>
        getJsBody(response)
          .map(getInfluxPoints)
          .map(seq => QueryResult.successful[JsArray](code, seq))
      case other =>
        errorHandler(other, response)
          .map(ex => QueryResult.failed[JsArray](other, ex))
    }
  }

  def toBulkQueryJsResult(response: Response[JsObject]): Future[QueryResult[Seq[JsArray]]] = {
    response.code.intValue() match {
      case code if isSuccessful(code) =>
        getJsBody(response)
          .map(getBulkInfluxValue)
          .map(seq => QueryResult.successful[Seq[JsArray]](code, seq))
      case other =>
        errorHandler(other, response)
          .map(ex => QueryResult.failed[Seq[JsArray]](other, ex))
    }
  }

  def getError(response: Response[JsObject]): Future[String] = {
    getJsBody(response)
      .map(_.getFields("error").head.convertTo[String])
  }

  def getErrorOpt(response: Response[JsObject]): Future[Option[String]] = {
    getJsBody(response)
      .map(
        _.getFields("results")
          .headOption
          .flatMap(_.convertTo[Seq[JsObject]].headOption)
          .flatMap(_.fields.get("error"))
          .map(_.convertTo[String]))
  }
}

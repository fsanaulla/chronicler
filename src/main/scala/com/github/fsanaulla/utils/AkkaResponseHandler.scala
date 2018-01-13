package com.github.fsanaulla.utils

import akka.http.scaladsl.model.HttpResponse
import akka.stream.ActorMaterializer
import com.github.fsanaulla.handlers.ResponseHandler
import com.github.fsanaulla.model._
import com.github.fsanaulla.utils.JsonSupport._
import spray.json.{JsArray, JsObject}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 31.07.17
  */
private[fsanaulla] trait AkkaResponseHandler extends ResponseHandler[HttpResponse] {

//  implicit val ex: ExecutionContext
  protected implicit val mat: ActorMaterializer

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

  def getError(response: HttpResponse)(implicit ex: ExecutionContext,
                                       mat: ActorMaterializer): Future[String] = {
    getJsBody(response)
      .map(_.getFields("error").head.convertTo[String])
  }

  def getErrorOpt(response: HttpResponse)(implicit ex: ExecutionContext,
                                          mat: ActorMaterializer): Future[Option[String]] = {
    getJsBody(response)
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

  private def errorHandler(code: Int,
                           response: HttpResponse)(implicit ex: ExecutionContext,
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

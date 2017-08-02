package com.fsanaulla.utils

import akka.http.scaladsl.model.HttpResponse
import akka.stream.ActorMaterializer
import com.fsanaulla.model._
import com.fsanaulla.utils.JsonSupport._
import spray.json.{JsArray, JsObject}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Failure
/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 31.07.17
  */
private[fsanaulla] trait ResponseWrapper {

  def toSingleJsResult(response: HttpResponse)(implicit ex: ExecutionContext, mat: ActorMaterializer): Future[Seq[JsArray]] = {
    unmarshalBody(response).map(getInfluxValue)
  }

  def toBulkJsResult(response: HttpResponse)(implicit ex: ExecutionContext, mat: ActorMaterializer): Future[Seq[Seq[JsArray]]] = {
    unmarshalBody(response)
      .map(_.getFields("results").head.convertTo[Seq[JsObject]])
      .map(_.map(_.getFields("series").head.convertTo[Seq[JsObject]].head))
      .map(_.map(_.getFields("values").head.convertTo[Seq[JsArray]]))
  }

  def toQueryResult[T](response: HttpResponse)(implicit ex: ExecutionContext, mat: ActorMaterializer, reader: InfluxReader[T]): Future[Seq[T]] = {
    toQueryJsResult(response).map(_.map(reader.read))
  }

  def toQueryJsResult(response: HttpResponse)(implicit ex: ExecutionContext, mat: ActorMaterializer): Future[Seq[JsArray]] = {
    response.status.intValue() match {
      case code if isSuccess(code) => toSingleJsResult(response)
      case other => errorHandler(other, response).map(ex => throw ex)
    }
  }

  def toBulkQueryJsResult(response: HttpResponse)(implicit ex: ExecutionContext, mat: ActorMaterializer): Future[Seq[Seq[JsArray]]] = {
    response.status.intValue() match {
      case code if isSuccess(code) => toBulkJsResult(response)
      case other => errorHandler(other, response).map(ex => throw ex)
    }
  }

  def toResult(response: HttpResponse)(implicit ex: ExecutionContext, mat: ActorMaterializer): Future[Unit] = {
    response.status.intValue() match {
      case code if isSuccess(code) => Future.successful({})
      case other => errorHandler(other, response).map(ex => Failure(ex))
    }
  }

  private def isSuccess(code: Int) = if (code >= 200 && code < 300) true else false

  private def errorHandler(code: Int, response: HttpResponse)(implicit ex: ExecutionContext, mat: ActorMaterializer): Future[InfluxException] = code match {
    case 400 => getError(response).map(errMsg => new BadRequestException(errMsg))
    case 404 => getError(response).map(errMsg => new ResourceNotFoundException(errMsg))
    case code: Int if code < 599 && code >= 500 => getError(response).map(errMsg => new InternalServerError(errMsg))
    case other => getError(response).map(errMsg => new UnknownException(errMsg))
  }

  private def getError(response: HttpResponse)(implicit ex: ExecutionContext, mat: ActorMaterializer): Future[String] = unmarshalBody(response).map(_.getFields("error").head.convertTo[String])
}


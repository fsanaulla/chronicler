package com.fsanaulla.utils

import akka.http.scaladsl.model.HttpResponse
import akka.stream.ActorMaterializer
import com.fsanaulla.model._
import spray.json.{JsArray, JsObject}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 31.07.17
  */
trait ResponseWrapper extends JsonSupport {

  def toSingleResult(response: HttpResponse)(implicit ex: ExecutionContext, mat: ActorMaterializer): Future[Seq[JsArray]] = {
    unmarshalBody(response).map(getInfluxValue)
  }

  def toBulkResult(response: HttpResponse)(implicit ex: ExecutionContext, mat: ActorMaterializer): Future[Seq[Seq[JsArray]]] = {
    unmarshalBody(response)
      .map(_.getFields("results").head.convertTo[Seq[JsObject]])
      .map(_.map(_.getFields("series").head.convertTo[Seq[JsObject]].head))
      .map(_.map(_.getFields("values").head.convertTo[Seq[JsArray]]))
  }

  def toQueryResponse[T](response: HttpResponse, result: => Future[Seq[T]])(implicit ex: ExecutionContext, mat: ActorMaterializer): Future[Seq[T]] = {
    response.status.intValue() match {
      case 200 => result
      case other => errorHandler(other, response).map(ex => throw ex)
    }
  }

  def toResponse[T <: InfluxResult](response: HttpResponse, result: => T)(implicit ex: ExecutionContext, mat: ActorMaterializer): Future[T] = {
    response.status.intValue() match {
      case 200 => Future.successful(result)
      case 204 => Future.successful(result)
      case other => errorHandler(other, response).map(ex => throw ex)
    }
  }

  private def errorHandler(code: Int, response: HttpResponse)(implicit ex: ExecutionContext, mat: ActorMaterializer): Future[InfluxException] = code match {
    case 400 => getError(response).map(errMsg => new BadRequestException(errMsg))
    case 404 => getError(response).map(errMsg => new ResourceNotFoundException(errMsg))
    case code: Int if code < 599 && code >= 500 => getError(response).map(errMsg => new InternalServerError(errMsg))
    case other => getError(response).map(errMsg => new UnknownException(errMsg))
  }

  private def getError(response: HttpResponse)(implicit ex: ExecutionContext, mat: ActorMaterializer): Future[String] = unmarshalBody(response).map(_.getFields("error").head.convertTo[String])
}


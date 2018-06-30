package com.github.fsanaulla.chronicler.async.handlers

import com.github.fsanaulla.chronicler.core.handlers.ResponseHandler
import com.github.fsanaulla.chronicler.core.model._
import com.softwaremill.sttp.Response
import jawn.ast.{JArray, JValue}

import scala.concurrent.Future
import scala.reflect.ClassTag

private[async] trait AsyncResponseHandler
  extends ResponseHandler[Future, Response[JValue]] with AsyncJsonHandler {

  // Simply result's
  override def toResult(response: Response[JValue]): Future[WriteResult] = {
    response.code match {
      case code if isSuccessful(code) && code != 204 =>
        getOptResponseError(response) map {
          case Some(msg) =>
            WriteResult.failed(code, new OperationException(msg))
          case _ =>
            WriteResult.successful(code)
        }
      case 204 =>
        WriteResult.successfulFuture(204)
      case other =>
        errorHandler(response, other)
          .map(ex => WriteResult.failed(other, ex))
    }
  }

  override def toComplexQueryResult[A: ClassTag, B: ClassTag](
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
        errorHandler(response, other)
          .map(ex => QueryResult.failed[B](other, ex))
    }
  }

  override def toQueryJsResult(response: Response[JValue]): Future[QueryResult[JArray]] = {
    response.code.intValue() match {
      case code if isSuccessful(code) =>
        getResponseBody(response)
          .map(getOptQueryResult)
          .map {
            case Some(seq) => QueryResult.successful[JArray](code, seq)
            case _ => QueryResult.empty[JArray](code)}
      case other =>
        errorHandler(response, other)
          .map(ex => QueryResult.failed[JArray](other, ex))
    }
  }

  override def toGroupedJsResult(response: Response[JValue]): Future[GroupedResult[JArray]] = {
    response.code.intValue() match {
      case code if isSuccessful(code) =>
        getResponseBody(response)
          .map(getOptGropedResult)
          .map {
            case Some(arr) => GroupedResult.successful[JArray](code, arr)
            case _ => GroupedResult.empty[JArray](code)}
      case other =>
        errorHandler(response, other)
          .map(ex => GroupedResult.failed[JArray](other, ex))
    }
  }

  override def toBulkQueryJsResult(response: Response[JValue]): Future[QueryResult[Array[JArray]]] = {
    response.code.intValue() match {
      case code if isSuccessful(code) =>
        getResponseBody(response)
          .map(getOptBulkInfluxPoints)
          .map {
            case Some(seq) => QueryResult.successful[Array[JArray]](code, seq)
            case _ => QueryResult.empty[Array[JArray]](code)
          }
      case other =>
        errorHandler(response, other)
          .map(ex => QueryResult.failed[Array[JArray]](other, ex))
    }
  }

  override def toQueryResult[A: ClassTag](response: Response[JValue])(implicit reader: InfluxReader[A]): Future[QueryResult[A]] =
    toQueryJsResult(response).map(_.map(reader.read))


  override def errorHandler(response: Response[JValue], code: Int): Future[InfluxException] = code match {
    case 400 =>
      getResponseError(response).map(errMsg => new BadRequestException(errMsg))
    case 401 =>
      getResponseError(response).map(errMsg => new AuthorizationException(errMsg))
    case 404 =>
      getResponseError(response).map(errMsg => new ResourceNotFoundException(errMsg))
    case code: Int if code < 599 && code >= 500 =>
      getResponseError(response).map(errMsg => new InternalServerError(errMsg))
    case _ =>
      getResponseError(response).map(errMsg => new UnknownResponseException(errMsg))
  }
}

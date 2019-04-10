/*
 * Copyright 2017-2019 Faiaz Sanaulla
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.fsanaulla.chronicler.akka.shared.handlers

import com.github.fsanaulla.chronicler.core.alias.{ErrorOr, ResponseCode}
import com.github.fsanaulla.chronicler.core.either
import com.github.fsanaulla.chronicler.core.model._
import com.github.fsanaulla.chronicler.core.typeclasses.{JsonHandler, ResponseHandler}
import com.softwaremill.sttp.Response
import jawn.ast.{JArray, JValue}

import scala.reflect.ClassTag

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 15.03.18
  */
private[akka] final class AkkaResponseHandler(implicit jsHandler: JsonHandler[Response[JValue]])
  extends ResponseHandler[Response[JValue]]  {

  override def toPingResult(response: Response[JValue]): ErrorOr[InfluxDBInfo] = {
    if (isPingCode(response.code)) jsHandler.pingHeaders(response)
    else Left(errorHandler(response, response.code))
  }

  override def toWriteResult(response: Response[JValue]): ErrorOr[ResponseCode] = {
    response.code match {
      case code if isSuccessful(code) && code != 204 =>
        jsHandler
          .responseErrorMsgOpt(response)
          .flatMap(_.fold[ErrorOr[ResponseCode]](Right(code))(str => Left(new OperationException(str))))
      case 204 =>
        Right(204)
      case other =>
        Left(errorHandler(response, other))
    }
  }

  override def toComplexQueryResult[A: ClassTag: InfluxReader, B: ClassTag](response: Response[JValue],
                                                                            f: (String, Array[A]) => B): ErrorOr[Array[B]] = {
    response.code match {
      case code if isSuccessful(code) =>
        jsHandler.responseBody(response)
          .flatMap(jsHandler.groupedSystemInfo[A])
          .map(_.map { case (dbName, queries) => f(dbName, queries) })
      case other =>
        Left(errorHandler(response, other))
    }
  }

  override def toQueryJsResult(response: Response[JValue]): ErrorOr[Array[JArray]] = {
    response.code.intValue() match {
      case code if isSuccessful(code) =>
        jsHandler.responseBody(response).flatMap(jsHandler.queryResult)
      case other =>
        Left(errorHandler(response, other))
    }
  }

  override def toGroupedJsResult(response: Response[JValue]): ErrorOr[Array[(Array[String], JArray)]] = {
    response.code.intValue() match {
      case code if isSuccessful(code) =>
        jsHandler.responseBody(response).flatMap(jsHandler.gropedResult)
      case other =>
        Left(errorHandler(response, other))
    }
  }

  override def toBulkQueryJsResult(response: Response[JValue]): ErrorOr[Array[Array[JArray]]] = {
    response.code.intValue() match {
      case code if isSuccessful(code) =>
        jsHandler.responseBody(response).flatMap(jsHandler.bulkResult)
      case other =>
        Left(errorHandler(response, other))
    }
  }

  override def toQueryResult[A: ClassTag: InfluxReader](response: Response[JValue]): ErrorOr[Array[A]] =
    toQueryJsResult(response)
      .map(_.map(InfluxReader[A].read))
      .map(either.array)
      .joinRight


  override def errorHandler(response: Response[JValue], code: Int): Throwable = {
    val error = code match {
      case 400 =>
        jsHandler.responseErrorMsg(response).map(new BadRequestException(_))
      case 401 =>
        jsHandler.responseErrorMsg(response).map(new AuthorizationException(_))
      case 404 =>
        jsHandler.responseErrorMsg(response).map(new ResourceNotFoundException(_))
      case code: Int if code < 599 && code >= 500 =>
        jsHandler.responseErrorMsg(response).map(new InternalServerError(_))
      case _ =>
        jsHandler.responseErrorMsg(response).map(new UnknownResponseException(_))
    }

    // merging parsing level issues with bad request issues
    error.merge
  }
}

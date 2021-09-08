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

package com.github.fsanaulla.chronicler.core.components

import com.github.fsanaulla.chronicler.core.alias.{ErrorOr, ResponseCode, Tags, Values}
import com.github.fsanaulla.chronicler.core.either
import com.github.fsanaulla.chronicler.core.either._
import com.github.fsanaulla.chronicler.core.model._
import com.github.fsanaulla.chronicler.core.typeclasses.{Functor, Apply}
import org.typelevel.jawn.ast.JArray

import scala.reflect.ClassTag

/**
  * Response handling functionality, it's provide method's that generalize
  * response handle flow, for every backend implementation
  *
  * @tparam R - Backend HTTP response type, for example for Akka HTTP backend - HttpResponse
  */
class ResponseHandlerBase[G[_], R](
    jsonHandler: JsonHandler[G, R]
)(implicit F: Functor[G], A: Apply[G]) {

  /**
    * Handling ping response
    *
    * @since 0.5.1
    */
  final def pingResult(response: R): G[ErrorOr[InfluxDBInfo]] = {
    if (isPingCode(jsonHandler.responseCode(response))) A.pure(jsonHandler.databaseInfo(response))
    else F.map(errorHandler(response))(Left(_))
  }

  /**
    * Method for handling HTTP responses with empty body
    *
    * @param response - backend response value
    * @return         - Result in future container
    */
  final def writeResult(response: R): G[ErrorOr[ResponseCode]] = {
    jsonHandler.responseCode(response) match {
      case code if isSuccessful(code) && code != 204 =>
        F.map(jsonHandler.responseErrorMsgOpt(response)) { ethErr =>
          ethErr.flatMapRight { err =>
            err.fold[ErrorOr[ResponseCode]](Right(code))(str => Left(InfluxException(code, str)))
          }
        }
      case 204 =>
        A.pure(Right(204))
      case 401 =>
        A.pure(Left(new InfluxException(401, "Authorized")))
      case _ =>
        F.map(errorHandler(response))(Left(_))
    }
  }

  /**
    * Handling HTTP responses with on fly body deserialization into JArray value
    *
    * @param response - backend response value
    * @return         - Query result of JArray in future container
    */
  final def queryResultJson(response: R): G[ErrorOr[Array[JArray]]] = {
    jsonHandler.responseCode(response) match {
      case code if isSuccessful(code) =>
        F.map(jsonHandler.responseBody(response)) { body =>
          body.mapRight { json =>
            jsonHandler.queryResult(json) match {
              case Some(arr) => arr
              case _         => Array.empty[JArray]
            }
          }
        }
      case 401 =>
        A.pure(Left(new InfluxException(401, "Authorized")))
      case _ =>
        F.map(errorHandler(response))(Left(_))
    }
  }

  /**
    * Handling HTTP response with GROUP BY clause in the query
    *
    * @param response - backend response
    * @return         - grouped result
    */
  final def groupedResultJson(response: R): G[ErrorOr[Array[(Tags, Values)]]] = {
    jsonHandler.responseCode(response) match {
      case code if isSuccessful(code) =>
        F.map(jsonHandler.responseBody(response)) { ethRes =>
          ethRes.mapRight { jv =>
            jsonHandler.groupedResult(jv) match {
              case Some(arr) => arr
              case _         => Array.empty
            }
          }
        }
      case 401 =>
        A.pure(Left(new InfluxException(401, "Authorized")))
      case _ =>
        F.map(errorHandler(response))(Left(_))
    }
  }

  /**
    * Method for handling HTtp responses with non empty body, that contains multiple response.
    *
    * deserialize to Seq[JArray]
    *
    * @param response - backend response value
    * @return         - Query result with multiple response values
    */
  final def bulkQueryResultJson(response: R): G[ErrorOr[Array[Array[JArray]]]] =
    jsonHandler.responseCode(response) match {
      case code if isSuccessful(code) =>
        F.map(jsonHandler.responseBody(response)) { ethRes =>
          ethRes.mapRight(
            resp =>
              jsonHandler.bulkResult(resp) match {
                case Some(arr) => arr
                case _         => Array.empty
              }
          )
        }
      case 401 =>
        A.pure(Left(new InfluxException(401, "Authorized")))
      case _ =>
        F.map(errorHandler(response))(Left(_))
    }

  /**
    * Extract HTTP response body, and transform it to A
    *
    * @param response backend response
    * @tparam A - Deserializer entity type
    * @return - Query result in future container
    */
  final def queryResult[A: ClassTag](
      response: R
  )(implicit rd: InfluxReader[A]): G[ErrorOr[Array[A]]] =
    F.map(queryResultJson(response)) { jvRes =>
      jvRes
        .mapRight(_.map(rd.read))
        .mapRight(either.array[Throwable, A])
        .joinRight
    }

  /***
    * Handler error codes by it's value
    *
    * @param response - response for extracting error message
    * @return         - InfluxException wrraped in container type
    */
  final def errorHandler(response: R): G[Throwable] =
    F.map(jsonHandler.responseErrorMsg(response)) { ethErr =>
      ethErr
        .mapRight(InfluxException(jsonHandler.responseCode(response), _))
        // merging parsing level issues with request level issues
        .merge
    }

  /***
    * Check response for success
    *
    * @param code - response code
    * @return     - is it success
    */
  final def isSuccessful(code: Int): Boolean = code >= 200 && code < 300

  /***
    * Check for ping response status code
    *
    * @param code - response code
    */
  final def isPingCode(code: Int): Boolean = code == 200 || code == 204
}

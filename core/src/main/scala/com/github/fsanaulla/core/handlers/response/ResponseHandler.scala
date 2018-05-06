package com.github.fsanaulla.core.handlers.response

import com.github.fsanaulla.core.model._
import jawn.ast.JArray

import scala.concurrent.Future
import scala.reflect.ClassTag

/**
  * This trait define response handling functionality, it's provide method's that generalize
  * response handle flow, for every backend implementation
  * @tparam R - Backend HTTP response type, for example for Akka HTTP backend - HttpResponse
  */
private[fsanaulla] trait ResponseHandler[R] extends ResponseHandlerHelper[R] {
  self: Executable =>

  /**
    * Method for handling HTTP responses with empty body
    * @param response - backend response value
    * @return - Result in future container
    */
  def toResult(response: R): Future[Result]

  /**
    * Method for handling HTTP responses with body, with on fly deserialization into JArray value
    * @param response - backaend response value
    * @return - Query result of JArray in future container
    */
  def toQueryJsResult(response: R): Future[QueryResult[JArray]]

  /**
    * Method for handling HTtp responses with non empty body, that contains multiple response.
    * deserialized into Seq[JArray]
    * @param response - backend response value
    * @return - Query result with multiple response values
    */
  def toBulkQueryJsResult(response: R): Future[QueryResult[Array[JArray]]]

  /**
    * Method for handling Info based HTTP responses, with possibility for future deserialization.
    * @param response - backend response value
    * @param f - function that transform into value of type [B]
    * @param reader - influx reader
    * @tparam A - entity for creating full Info object
    * @tparam B - info object
    * @return - Query result of [B] in future container
    */
  def toComplexQueryResult[A: ClassTag, B: ClassTag](response: R, f: (String, Array[A]) => B)(implicit reader: InfluxReader[A]): Future[QueryResult[B]]

  /**
    * Extract error message from failed response
    * @param response Response
    * @return - Error message
    */
  def getError(response: R): Future[String]

  /**
    * Extract if exist eroor message from response
    * @param response - Response
    * @return - optional error message
    */
  def getErrorOpt(response: R): Future[Option[String]]
}

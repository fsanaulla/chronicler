package com.github.fsanaulla.core.handlers.json

import com.github.fsanaulla.core.model.Executable
import com.github.fsanaulla.core.utils.Extensions.RichJValue
import jawn.ast.JValue

import scala.concurrent.Future

/***
  * Trait that define all necessary methods for handling JSON related operation
  * @tparam R - Response type
  */
private[fsanaulla] trait JsonHandler[R] extends JsonHandlerHelper with Executable {

  /***
    * Extracting JSON from Response
    * @param response - Response
    * @return         - Extracted JSON
    */
  def getJsBody(response: R): Future[JValue]

  /**
    * Extract error message from response
    * @param response - Response
    * @return         - Error Message
    */
  def getError(response: R): Future[String] =
    getJsBody(response).map(_.get("error").asString)

  /**
    * Extract optional error message from response
    * @param response - Response
    * @return         - optional error message
    */
  def getErrorOpt(response: R): Future[Option[String]] = {
    getJsBody(response)
      .map(_.get("results").arrayValue.flatMap(_.headOption))
      .map(_.flatMap(_.get("error").getString))
  }
}
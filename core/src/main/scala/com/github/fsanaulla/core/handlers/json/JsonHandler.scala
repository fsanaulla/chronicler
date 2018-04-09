package com.github.fsanaulla.core.handlers.json

import jawn.ast.JValue

import scala.concurrent.Future

/***
  * Trait that define all necessary methods for handling JSON related operation
  * @tparam R - Response type
  */
private[fsanaulla] trait JsonHandler[R] extends JsonHandlerHelper {

  /***
    * Extracting JSON from Response
    * @param response - Response
    * @return - Extracted JSON
    */
  def getJsBody(response: R): Future[JValue]
}

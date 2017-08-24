package com.fsanaulla.model

import spray.json.JsArray

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 30.07.17
  */
case class Result(code: Int, isSuccess: Boolean, ex: Option[Throwable] = None)

object Result {
  def successful(code: Int) = Result(code, isSuccess = true, None)
}

case class QueryResult[T](code: Int, isSuccess: Boolean, queryResult: Seq[T] = Nil, ex: Option[Throwable] = None)
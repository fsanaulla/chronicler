package com.fsanaulla.model

import spray.json.JsArray

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 30.07.17
  */
sealed trait InfluxResult {
  val code: Int
  val isSuccess: Boolean
  val ex: Option[Throwable]
}

case class Result(code: Int, isSuccess: Boolean, ex: Option[Throwable] = None) extends InfluxResult

case class QueryResult[T](code: Int, isSuccess: Boolean, queryResult: Seq[T] = Nil, ex: Option[Throwable] = None) extends InfluxResult
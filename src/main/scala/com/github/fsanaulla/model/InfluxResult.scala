package com.github.fsanaulla.model

import spray.json.JsArray

import scala.concurrent.Future

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 30.07.17
  */
case class Result(code: Int, isSuccess: Boolean, ex: Option[Throwable] = None)

object Result {

  def successful(code: Int): Result = {
    Result(code, isSuccess = true, None)
  }

  def failed(code: Int, ex: Throwable): Result = {
    Result(code, isSuccess = false, Some(ex))
  }

  def successfulFuture(code: Int): Future[Result] = {
    Future.successful(successful(code))
  }
}

case class QueryResult[A](code: Int,
                          isSuccess: Boolean,
                          queryResult: Seq[A] = Nil,
                          ex: Option[Throwable] = None)

object QueryResult {

  def successful[A](code: Int, seq: Seq[A]): QueryResult[A] = {
    QueryResult[A](code, isSuccess = true, seq)
  }

  def failed[A](code: Int, ex: Throwable): QueryResult[A] = {
    QueryResult[A](code, isSuccess = false, ex = Some(ex))
  }
}

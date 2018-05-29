package com.github.fsanaulla.core.model

import scala.concurrent.Future
import scala.reflect.ClassTag

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 30.07.17
  */

/** Entity that represent result types */
sealed trait InfluxResult {
  def code: Int
  def isSuccess: Boolean
  def ex: Option[Throwable]
}

/**
  * Entity to resresent non-query result
  * @param code      - HTTP response code
  * @param isSuccess - is it complete successfully
  * @param ex        - optional exception
  */
case class Result(code: Int, isSuccess: Boolean, ex: Option[Throwable] = None) extends InfluxResult

object Result {
  /** Create successful result entity */
  def successful(code: Int): Result = Result(code, isSuccess = true, None)
  /** Create failed result entity */
  def failed(code: Int, ex: Throwable): Result = Result(code, isSuccess = false, Some(ex))
  /** Successfully created result inside compiled Future */
  def successfulFuture(code: Int): Future[Result] = Future.successful(successful(code))
}

/**
  * Entity to represent query result response
  * @param code        - HTTP response code
  * @param isSuccess   - is it complete successfully
  * @param queryResult - seq of queried results
  * @param ex          - optional exception
  * @tparam A          - which entity should be retrieved from query request
  */
case class QueryResult[A](
                           code: Int,
                           isSuccess: Boolean,
                           queryResult: Array[A],
                           ex: Option[Throwable] = None) extends InfluxResult {

  /** map inner result entities */
  def map[B: ClassTag](f: A => B): QueryResult[B] =
    QueryResult[B](code, isSuccess, queryResult.map(f), ex)
}

object QueryResult {
  /** Create successful query result */
  def successful[A](code: Int, arr: Array[A]): QueryResult[A] =
    QueryResult[A](code, isSuccess = true, arr)
  /** Create successful empty query result */
  def empty[A: ClassTag](code: Int): QueryResult[A] = successful(code, Array.empty[A])
  /** Create failed empty query result */
  def failed[A: ClassTag](code: Int, ex: Throwable): QueryResult[A] =
    QueryResult[A](code, isSuccess = false, Array.empty[A], ex = Some(ex))

}

package com.github.fsanaulla.core.model

import scala.concurrent.Future
import scala.reflect.ClassTag

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 30.07.17
  */
case class Result(code: Int,
                  isSuccess: Boolean,
                  ex: Option[Throwable] = None)

object Result {

  def successful(code: Int): Result = Result(code, isSuccess = true, None)

  def failed(code: Int, ex: Throwable): Result = Result(code, isSuccess = false, Some(ex))

  def successfulFuture(code: Int): Future[Result] = Future.successful(successful(code))
}

case class QueryResult[A](code: Int,
                          isSuccess: Boolean,
                          queryResult: Array[A],
                          ex: Option[Throwable] = None) {

  def transform[B: ClassTag](f: A => B): QueryResult[B] = {
    if (queryResult.nonEmpty) {
      QueryResult[B](code, isSuccess, queryResult.map(f), ex)
    } else QueryResult[B](code, isSuccess, Array.empty[B], ex)
  }
}

object QueryResult {

  def successful[A](code: Int, arr: Array[A]): QueryResult[A] =
    QueryResult[A](code, isSuccess = true, arr)

  def empty[A: ClassTag](code: Int): QueryResult[A] = successful(code, Array.empty[A])

  def failed[A: ClassTag](code: Int, ex: Throwable): QueryResult[A] =
    QueryResult[A](code, isSuccess = false, Array.empty[A], ex = Some(ex))

}

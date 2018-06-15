package com.github.fsanaulla.chronicler.core.model

import scala.concurrent.Future
import scala.reflect.ClassTag
import scala.util.{Success, Try}

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

// it's temporary solution, until 0.3.0 and type-safe DSL
/**
  * Root object of read result AST
  * @tparam A
  */
sealed trait ReadResult[A] extends InfluxResult {
  def result: Array[A]
  def groupedResult: Array[(Array[String], A)]
}

/**
  * Entity to resresent non-query result
  * @param code      - HTTP response code
  * @param isSuccess - is it complete successfully
  * @param ex        - optional exception
  */
final case class WriteResult(code: Int, isSuccess: Boolean, ex: Option[Throwable] = None) extends InfluxResult

object WriteResult {
  /** Create successful result entity */
  def successful(code: Int): WriteResult = WriteResult(code, isSuccess = true, None)
  /** Create failed result entity */
  def failed(code: Int, ex: Throwable): WriteResult = WriteResult(code, isSuccess = false, Some(ex))
  /** Successfully created result inside compiled Future */
  def successfulFuture(code: Int): Future[WriteResult] = Future.successful(successful(code))
  /** Successfully created result inside scala.util.Success */
  def successfulTry(code: Int): Try[WriteResult] = Success(successful(code))
}

/**
  * Entity to represent query result response
  * @param code        - HTTP response code
  * @param isSuccess   - is it complete successfully
  * @param result      - array of queried results
  * @param ex          - optional exception
  * @tparam A          - which entity should be retrieved from query request
  */
final case class QueryResult[A](
                                 code: Int,
                                 isSuccess: Boolean,
                                 result: Array[A],
                                 ex: Option[Throwable] = None) extends ReadResult[A] {

  def map[B: ClassTag](f: A => B): QueryResult[B] =
    QueryResult[B](code, isSuccess, result.map(f), ex)

  override def groupedResult: Array[(Array[String], A)] =
    throw new UnsupportedOperationException("Grouped result unsupported in query result")
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

/**
  * Entity to represent grouped result response
  * @param code          - HTTP response code
  * @param isSuccess     - is it complete successfully
  * @param groupedResult - array of group by results
  * @param ex            - exception if exist
  * @tparam A            - which entity should be retrieveed from query result
  */
final case class GroupedResult[A](
                                   code: Int,
                                   isSuccess: Boolean,
                                   groupedResult: Array[(Array[String], A)],
                                   ex: Option[Throwable] = None) extends ReadResult[A] {
  def map[B: ClassTag](f: A => B): GroupedResult[B] =
    GroupedResult[B](code, isSuccess, groupedResult.map(p => p._1 -> f(p._2)), ex)

  override def result: Array[A] =
    throw new UnsupportedOperationException("Query result unsupported in grouped result")
}

object GroupedResult {
  /** Create successful query result */
  def successful[A](code: Int, arr: Array[(Array[String], A)]): GroupedResult[A] =
    GroupedResult[A](code, isSuccess = true, arr)
  /** Create successful empty query result */
  def empty[A: ClassTag](code: Int): GroupedResult[A] =
    successful(code, Array.empty[(Array[String], A)])
  /** Create failed empty query result */
  def failed[A: ClassTag](code: Int, ex: Throwable): GroupedResult[A] =
    GroupedResult[A](code, isSuccess = false, Array.empty[(Array[String], A)], Some(ex))
}

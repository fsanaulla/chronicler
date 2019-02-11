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

package com.github.fsanaulla.chronicler.core.model

import scala.reflect.ClassTag

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 30.07.17
  */

/** Entity that represent result types */
sealed trait InfluxResult extends scala.Serializable {
  def code: Int
  def isSuccess: Boolean
  def ex: Option[Throwable]
}

/**
  * Root object of read result AST
  */
sealed trait ReadResult[A] extends InfluxResult {
  def queryResult: Array[A]
  def groupedResult: Array[(Array[String], A)]
  def map[B: ClassTag](f: A => B): ReadResult[B]
}

/**
  * Entity to represent non-query result
  *
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
}

/**
  * Entity to represent query result response
  *
  * @param code        - HTTP response code
  * @param isSuccess   - is it complete successfully
  * @param queryResult - array of queried results
  * @param ex          - optional exception
  * @tparam A          - which entity should be retrieved from query request
  */
final case class QueryResult[A](code: Int,
                                isSuccess: Boolean,
                                queryResult: Array[A],
                                ex: Option[Throwable] = None) extends ReadResult[A] {

  def map[B: ClassTag](f: A => B): QueryResult[B] =
    QueryResult[B](code, isSuccess, queryResult.map(f), ex)

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
  *
  * @param code          - HTTP response code
  * @param isSuccess     - is it complete successfully
  * @param groupedResult - array of group by results
  * @param ex            - exception if exist
  * @tparam A            - which entity should be retrieved from query result
  */
final case class GroupedResult[A](code: Int,
                                  isSuccess: Boolean,
                                  groupedResult: Array[(Array[String], A)],
                                  ex: Option[Throwable] = None) extends ReadResult[A] {
  def map[B: ClassTag](f: A => B): GroupedResult[B] =
    GroupedResult[B](
      code,
      isSuccess,
      // map values
      groupedResult.map { case (tags, values) => tags -> f(values) },
      ex
    )

  override def queryResult: Array[A] =
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

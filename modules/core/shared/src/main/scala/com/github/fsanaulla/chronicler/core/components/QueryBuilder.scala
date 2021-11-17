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

package com.github.fsanaulla.chronicler.core.components

/** Trait that define functionality for handling query building
  *
  * @tparam U
  *   - Result type parameter, for example for AkkaHttpBackend
  *   - used `akka.http.scaladsl.model.Uri`
  */
abstract class QueryBuilder[U] {

  def buildQuery(path: String): U

  /** Method that build result URI object of type [A], from uri path, and query parameters
    *
    * @param path
    *   - string based uri path
    * @param queryParam
    *   - query parameters that will be embedded into request
    * @return
    *   - URI object
    */
  def buildQuery(path: String, queryParam: (String, String)): U

  def buildQuery(path: String, queryParam: List[(String, String)]): U

  final def query(query: String): (String, String) =
    "q" -> query

  final def query(dbName: String, query: String): List[(String, String)] = List(
    "q"  -> query,
    "db" -> dbName
  )
}

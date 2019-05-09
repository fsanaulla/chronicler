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

import com.github.fsanaulla.chronicler.core.model.InfluxCredentials

import scala.collection.mutable

/**
  * Trait that define functionality for handling query building
  *
  * @tparam A - Result type parameter, for example for AkkaHttpBackend
  *           - used `akka.http.scaladsl.model.Uri`
  */
abstract class QueryBuilder[A](credentials: Option[InfluxCredentials]) {

  /**
    * Method that build result URI object of type [A], from uri path, and query parameters
    *
    * @param uri         - string based uri path
    * @param queryParams - query parameters that will be embedded into request
    * @return            - URI object
    */
  def buildQuery(uri: String, queryParams: Map[String, String]): A

  /**
    * Method that embed credentials to already created query parameters map
    *
    * @param queryMap - query parameters map
    * @return         - updated query parameters map with embedded credentials
    */
  final def buildQueryParams(queryMap: mutable.Map[String, String]): Map[String, String] = {
    for {
      c <- credentials
    } yield queryMap += ("u" -> c.username, "p" -> c.password)

    queryMap.toMap
  }

  /**
    * Produce query parameters map for string parameter, with embedding credentials
    *
    * @param query - query string parameter
    * @return      - query parameters
    */
  final def buildQueryParams(query: String): Map[String, String] =
    buildQueryParams(scala.collection.mutable.Map("q" -> query))
}

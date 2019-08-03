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

/**
  * Abstraction over request execution
  *
  * @tparam F - execution effect
  * @tparam R - response
  * @tparam U - request uri
  * @tparam B - request body
  */
trait RequestExecutor[F[_], R, U, B] {

  /**
    * Execute HTTP POST
    *
    * @param uri        - request uri
    * @param body       - request body
    * @param compressed - body compression flag
    */
  def post(
      uri: U,
      body: B,
      compressed: Boolean
    ): F[R]

  /**
    * Execute HTTP GET request
    *
    * @param uri        - request uri
    * @param compressed - body compression flag
    * */
  def get(uri: U, compressed: Boolean): F[R]
}

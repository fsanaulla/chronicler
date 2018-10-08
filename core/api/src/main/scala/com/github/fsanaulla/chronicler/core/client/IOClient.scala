/*
 * Copyright 2017-2018 Faiaz Sanaulla
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

package com.github.fsanaulla.chronicler.core.client

import com.github.fsanaulla.chronicler.core.api.{DatabaseIO, MeasurementIO}

import scala.reflect.ClassTag

/**
  * Define necessary methods for providing IO operations
  *
  * @tparam F - Response type container
  * @tparam E - which response entity should be user
  */
trait IOClient[F[_], E] extends AutoCloseable {

  /**
    * Get database instant
    *
    * @param dbName - database name
    * @return       - Backend related implementation of DatabaseIO
    */
  def database(dbName: String): DatabaseIO[F, E]

  /**
    * Get measurement instance with execution type A
    *
    * @param dbName          - on which database
    * @param measurementName - which measurement
    * @tparam A              - measurement entity type
    * @return                - Backend related implementation of MeasurementIO
    */
  def measurement[A: ClassTag](dbName: String, measurementName: String): MeasurementIO[F, A]
}

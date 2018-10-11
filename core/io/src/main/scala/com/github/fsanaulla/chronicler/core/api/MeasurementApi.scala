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

package com.github.fsanaulla.chronicler.core.api

import com.github.fsanaulla.chronicler.core.enums.{Consistency, Precision}
import com.github.fsanaulla.chronicler.core.model.{InfluxWriter, WriteResult}

/**
  * Main functionality for measurement api
  * @tparam E - Entity type
  * @tparam R - Request entity type
  */
trait MeasurementApi[F[_], E] {

  /**
    * Make single write
    * @param entity          - entity to write
    * @param consistency     - consistence level
    * @param precision       - time precision
    * @param retentionPolicy - retention policy type
    * @param writer          - implicit serializer to InfluxLine format
    * @return                - Write result on backend container
    */
  def write(entity: E,
            consistency: Consistency,
            precision: Precision,
            retentionPolicy: Option[String] = None)(implicit writer: InfluxWriter[E]): F[WriteResult]

  /**
    * Make bulk write
    * @param entities        - entities to write
    * @param consistency     - consistence level
    * @param precision       - time precision
    * @param retentionPolicy - retention policy type
    * @param writer          - implicit serializer to InfluxLine format
    * @return                - Write result on backend container
    */
  def bulkWrite(entities: Seq[E],
                consistency: Consistency,
                precision: Precision,
                retentionPolicy: Option[String] = None)(implicit writer: InfluxWriter[E]): F[WriteResult]
}

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

package com.github.fsanaulla.chronicler.core.io

import com.github.fsanaulla.chronicler.core.enums.{Consistency, Precision}
import com.github.fsanaulla.chronicler.core.model.WriteResult

/***
  * Define basic write operation for communicating with InfluxDB
  * @tparam M - container type
  * @tparam E - Entity type
  */
trait WriteOperations[M[_], E] {

  /**
    * Execute single write to InfluxDB
    * @param dbName          - For which database
    * @param entity          - Entity that should be entered
    * @param consistency     - Consistency level
    * @param precision       - Precision level
    * @param retentionPolicy - Optional retention policy name
    * @return                - Result of execution
    */
  private[chronicler] def writeTo(dbName: String,
                                  entity: E,
                                  consistency: Consistency,
                                  precision: Precision,
                                  retentionPolicy: Option[String],
                                  gzipped: Boolean): M[WriteResult]

  /**
    * Write points from specified file
    * @param filePath        - which file should be used as source
    * @param consistency     - consistency level
    * @param precision       - precision level
    * @param retentionPolicy - optional retention policy name
    * @return                - execution result
    */
  private[chronicler] def writeFromFile(dbName: String,
                                        filePath: String,
                                        consistency: Consistency,
                                        precision: Precision,
                                        retentionPolicy: Option[String],
                                        gzipped: Boolean): M[WriteResult]

}

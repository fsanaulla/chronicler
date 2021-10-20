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

package com.github.fsanaulla.chronicler.core.management.rp

import com.github.fsanaulla.chronicler.core.alias.{ErrorOr, ResponseCode}
import com.github.fsanaulla.chronicler.core.components._
import com.github.fsanaulla.chronicler.core.management.ManagementResponseHandler
import com.github.fsanaulla.chronicler.core.typeclasses.{FunctionK, MonadError}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 08.08.17
  */
trait RetentionPolicyManagement[F[_], G[_], Req, Resp, U, E]
    extends RetentionPolicyManagementQuery[U] {
  implicit val qb: QueryBuilder[U]
  implicit val rb: RequestBuilder[Req, U, E]
  implicit val re: RequestExecutor[F, Req, Resp]
  implicit val rh: ManagementResponseHandler[G, Resp]
  implicit val ME: MonadError[F, Throwable]
  implicit val FK: FunctionK[G, F]

  /**
    * Create retention policy for specified database
    * @param rpName        - retention policy name
    * @param dbName        - database name
    * @param duration      - retention policy duration
    * @param replication   - replication factor
    * @param shardDuration - shard duration value
    * @param default       - use default
    * @return              - execution result
    */
  final def createRetentionPolicy(
      rpName: String,
      dbName: String,
      duration: String,
      replication: Int = 1,
      shardDuration: Option[String] = None,
      default: Boolean = false
  ): F[ErrorOr[ResponseCode]] = {
    if (replication < 1) ME.fail(new Exception("Replication must greater that 0"))
    else {
      val uri  = createRPQuery(rpName, dbName, duration, replication, shardDuration, default)
      val req  = rb.get(uri, compress = false)
      val resp = re.execute(req)

      ME.flatMap(resp)(resp => FK(rh.writeResult(resp)))
    }
  }

  /** Update retention policy */
  final def updateRetentionPolicy(
      rpName: String,
      dbName: String,
      duration: Option[String] = None,
      replication: Option[Int] = None,
      shardDuration: Option[String] = None,
      default: Boolean = false
  ): F[ErrorOr[ResponseCode]] = {
    val uri  = updateRPQuery(rpName, dbName, duration, replication, shardDuration, default)
    val req  = rb.get(uri, compress = false)
    val resp = re.execute(req)

    ME.flatMap(resp)(resp => FK(rh.writeResult(resp)))
  }

  /** Drop retention policy */
  final def dropRetentionPolicy(rpName: String, dbName: String): F[ErrorOr[ResponseCode]] = {
    val uri  = dropRPQuery(rpName, dbName)
    val req  = rb.get(uri, compress = false)
    val resp = re.execute(req)

    ME.flatMap(resp)(resp => FK(rh.writeResult(resp)))
  }

  /** Show list of retention polices */
  final def showRetentionPolicies(dbName: String): F[ErrorOr[Array[RetentionPolicyInfo]]] = {
    val uri  = showRPQuery(dbName)
    val req  = rb.get(uri, compress = false)
    val resp = re.execute(req)

    ME.flatMap(resp)(resp => FK(rh.queryResult[RetentionPolicyInfo](resp)))
  }

}

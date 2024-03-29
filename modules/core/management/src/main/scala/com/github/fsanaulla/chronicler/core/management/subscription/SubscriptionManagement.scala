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

package com.github.fsanaulla.chronicler.core.management.subscription

import com.github.fsanaulla.chronicler.core.alias.{ErrorOr, ResponseCode}
import com.github.fsanaulla.chronicler.core.components._
import com.github.fsanaulla.chronicler.core.enums.Destination
import com.github.fsanaulla.chronicler.core.management.ManagementResponseHandler
import com.github.fsanaulla.chronicler.core.query.SubscriptionsManagementQuery
import com.github.fsanaulla.chronicler.core.typeclasses.{FunctionK, MonadError}

/** * Provide support of subscription api
  *
  * @tparam F
  *   - execution effect type
  * @tparam G
  *   - parsing effect type
  * @tparam Resp
  *   - response type
  * @tparam U
  *   - uri type
  * @tparam E
  *   - response entity type
  *
  * @see
  *   - https://docs.influxdata.com/influxdb/v1.7/administration/subscription-management/
  */
trait SubscriptionManagement[F[_], G[_], Req, Resp, U, E] extends SubscriptionsManagementQuery[U] {
  implicit val qb: QueryBuilder[U]
  implicit val rb: RequestBuilder[Req, U, E]
  implicit val re: RequestExecutor[F, Req, Resp]
  implicit val rh: ManagementResponseHandler[G, Resp]
  implicit val ME: MonadError[F, Throwable]
  implicit val FK: FunctionK[G, F]

  /** Create subscription
    * @param subsName
    *   - subscription name
    * @param dbName
    *   - database name
    * @param rpName
    *   - retention policy name
    * @param destinationType
    *   - destination type, where subscription should aggregate data
    * @param addresses
    *   - subscription addresses
    * @return
    *   - execution result
    */
  final def createSubscription(
      subsName: String,
      dbName: String,
      rpName: String = "autogen",
      destinationType: Destination,
      addresses: Seq[String]
  ): F[ErrorOr[ResponseCode]] = {
    val uri  = createSubscriptionQuery(subsName, dbName, rpName, destinationType, addresses)
    val req  = rb.get(uri, compress = false)
    val resp = re.execute(req)

    ME.flatMap(resp)(resp => FK(rh.writeResult(resp)))
  }

  /** Drop subscription */
  final def dropSubscription(
      subName: String,
      dbName: String,
      rpName: String
  ): F[ErrorOr[ResponseCode]] = {
    val uri  = dropSubscriptionQuery(subName, dbName, rpName)
    val req  = rb.get(uri, compress = false)
    val resp = re.execute(req)

    ME.flatMap(resp)(resp => FK(rh.writeResult(resp)))
  }

  /** Show list of subscription info */
  final def showSubscriptionsInfo: F[ErrorOr[Array[SubscriptionInfo]]] = {
    val uri  = showSubscriptionsQuery
    val req  = rb.get(uri, compress = false)
    val resp = re.execute(req)

    ME.flatMap(resp)(resp => FK(rh.toSubscriptionQueryResult(resp)))
  }
}

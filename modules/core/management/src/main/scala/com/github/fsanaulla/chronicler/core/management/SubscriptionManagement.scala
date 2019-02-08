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

package com.github.fsanaulla.chronicler.core.management

import com.github.fsanaulla.chronicler.core.enums.Destination
import com.github.fsanaulla.chronicler.core.implicits._
import com.github.fsanaulla.chronicler.core.model._
import com.github.fsanaulla.chronicler.core.query.SubscriptionsManagementQuery
import com.github.fsanaulla.chronicler.core.typeclasses.{FlatMap, QueryBuilder, RequestExecutor, ResponseHandler}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
private[chronicler] trait SubscriptionManagement[F[_], Req, Resp, Uri, Entity] extends SubscriptionsManagementQuery[Uri] {
  implicit val qb: QueryBuilder[Uri]
  implicit val re: RequestExecutor[F, Req, Resp, Uri]
  implicit val rh: ResponseHandler[F, Resp]
  implicit val fm: FlatMap[F]

  import re.buildRequest

  /**
    * Create subscription
    * @param subsName        - subscription name
    * @param dbName          - database name
    * @param rpName          - retention policy name
    * @param destinationType - destination type, where subscription should aggregate data
    * @param addresses       - subscription addresses
    * @return                - execution result
    */
  final def createSubscription(subsName: String,
                               dbName: String,
                               rpName: String = "autogen",
                               destinationType: Destination,
                               addresses: Seq[String]): F[WriteResult] =
    fm.flatMap(re.execute(createSubscriptionQuery(subsName, dbName, rpName, destinationType, addresses)))(rh.toResult)

  /** Drop subscription */
  final def dropSubscription(subName: String, dbName: String, rpName: String): F[WriteResult] =
    fm.flatMap(re.execute(dropSubscriptionQuery(subName, dbName, rpName)))(rh.toResult)

  /** Show list of subscription info */
  final def showSubscriptionsInfo: F[QueryResult[SubscriptionInfo]] =
    fm.flatMap(re.execute(showSubscriptionsQuery))(rh.toSubscriptionQueryResult)
}

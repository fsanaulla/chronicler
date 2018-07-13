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

package com.github.fsanaulla.chronicler.core.api.management

import com.github.fsanaulla.chronicler.core.enums.Destination
import com.github.fsanaulla.chronicler.core.handlers.{QueryHandler, RequestHandler, ResponseHandler}
import com.github.fsanaulla.chronicler.core.model._
import com.github.fsanaulla.chronicler.core.query.SubscriptionsManagementQuery
import com.github.fsanaulla.chronicler.core.utils.DefaultInfluxImplicits._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
private[chronicler] trait SubscriptionManagement[M[_], Req, Resp, Uri, Entity] extends SubscriptionsManagementQuery[Uri] {
  self: RequestHandler[M, Req, Resp, Uri]
    with ResponseHandler[M, Resp]
    with QueryHandler[Uri]
    with Mappable[M, Resp]
    with ImplicitRequestBuilder[Uri, Req]
    with HasCredentials =>

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
                               addresses: Seq[String]): M[WriteResult] =
    mapTo(execute(createSubscriptionQuery(subsName, dbName, rpName, destinationType, addresses)), toResult)

  /** Drop subscription */
  final def dropSubscription(subName: String, dbName: String, rpName: String): M[WriteResult] =
    mapTo(execute(dropSubscriptionQuery(subName, dbName, rpName)), toResult)

  /** Show list of subscription info */
  final def showSubscriptionsInfo: M[QueryResult[SubscriptionInfo]] =
    mapTo(execute(showSubscriptionsQuery()), toSubscriptionQueryResult)
}

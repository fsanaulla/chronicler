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

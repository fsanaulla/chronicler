package com.github.fsanaulla.chronicler.core.api.management

import com.github.fsanaulla.chronicler.core.enums.Destination
import com.github.fsanaulla.chronicler.core.handlers.{QueryHandler, RequestHandler, ResponseHandler}
import com.github.fsanaulla.chronicler.core.model._
import com.github.fsanaulla.chronicler.core.query.SubscriptionsManagementQuery

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
private[chronicler] trait SubscriptionManagement[M[_], R, U, E] extends SubscriptionsManagementQuery[U] {
  self: RequestHandler[M, R, U, E]
    with ResponseHandler[M, R]
    with QueryHandler[U]
    with Mappable[M, R]
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
    m.mapTo(readRequest(createSubscriptionQuery(subsName, dbName, rpName, destinationType, addresses)), toResult)

  /** Drop subscription */
  final def dropSubscription(subName: String, dbName: String, rpName: String): M[WriteResult] =
    m.mapTo(readRequest(dropSubscriptionQuery(subName, dbName, rpName)), toResult)

  /** Show list of subscription info */
  final def showSubscriptionsInfo: M[QueryResult[SubscriptionInfo]] =
    m.mapTo(readRequest(showSubscriptionsQuery()), toSubscriptionQueryResult)

//  /** Show subscription by database name */
//  def showSubscription(dbName: String): Future[QueryResult[Subscription]] = {
//
//    showSubscriptionsInfo().map { queryRes =>
//      val seq = queryRes
//        .queryResult
//        .find(_.dbName == dbName)
//        .map(_.subscriptions)
//        .getOrElse(Array.empty[Subscription])
//
//      QueryResult[Subscription](queryRes.code, queryRes.isSuccess, seq, queryRes.ex)
//    }
//  }

//  /** Update subscription */
//  def updateSubscription(
//                          subsName: String,
//                          dbName: String,
//                          rpName: String,
//                          destination: Destination = Destinations.ALL,
//                          address: Seq[String]): M[R] = {
//    for {
//      dropRes <- dropSubscription(subsName, dbName, rpName) if dropRes.ex.isEmpty
//      createRes <- createSubscription(subsName, dbName, rpName, destination, address)
//    } yield createRes
//  }
}

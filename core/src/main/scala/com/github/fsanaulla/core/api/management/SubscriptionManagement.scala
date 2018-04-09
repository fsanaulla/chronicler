package com.github.fsanaulla.core.api.management

import com.github.fsanaulla.core.enums.{Destination, Destinations}
import com.github.fsanaulla.core.handlers.RequestHandler
import com.github.fsanaulla.core.handlers.query.QueryHandler
import com.github.fsanaulla.core.handlers.response.ResponseHandler
import com.github.fsanaulla.core.model._
import com.github.fsanaulla.core.query.SubscriptionsManagementQuery
import com.github.fsanaulla.core.utils.DefaultInfluxImplicits._

import scala.concurrent.Future

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
private[fsanaulla] trait SubscriptionManagement[R, U, M, E] extends SubscriptionsManagementQuery[U] {
  self: RequestHandler[R, U, M, E]
    with ResponseHandler[R]
    with QueryHandler[U]
    with HasCredentials
    with Executable =>

  def createSubscription(subsName: String,
                         dbName: String,
                         rpName: String = "autogen",
                         destinationType: Destination,
                         addresses: Seq[String]): Future[Result] = {

    readRequest(createSubscriptionQuery(subsName, dbName, rpName, destinationType, addresses))
      .flatMap(toResult)
  }

  def dropSubscription(subName: String,
                       dbName: String,
                       rpName: String): Future[Result] = {

    readRequest(dropSubscriptionQuery(subName, dbName, rpName)).flatMap(toResult)
  }

  def showSubscriptionsInfo(): Future[QueryResult[SubscriptionInfo]] = {
    readRequest(showSubscriptionsQuery()).flatMap(toSubscriptionQueryResult)
  }

  def showSubscriptions(dbName: String): Future[QueryResult[Subscription]] = {

    showSubscriptionsInfo().map { queryRes =>
      val seq = queryRes
        .queryResult
        .find(_.dbName == dbName)
        .map(_.subscriptions)
        .getOrElse(Array.empty[Subscription])

      QueryResult[Subscription](queryRes.code, queryRes.isSuccess, seq, queryRes.ex)
    }
  }

  def updateSubscription(subsName: String,
                         dbName: String,
                         rpName: String,
                         destination: Destination = Destinations.ALL,
                         address: Seq[String]): Future[Result] = {
    for {
      dropRes <- dropSubscription(subsName, dbName, rpName) if dropRes.ex.isEmpty
      createRes <- createSubscription(subsName, dbName, rpName, destination, address)
    } yield createRes
  }

}

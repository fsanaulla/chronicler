package com.github.fsanaulla.api.management

import com.github.fsanaulla.handlers.{QueryHandler, RequestHandler, ResponseHandler}
import com.github.fsanaulla.model.InfluxImplicits._
import com.github.fsanaulla.model._
import com.github.fsanaulla.query.SubscriptionsManagementQuery
import com.github.fsanaulla.utils.constants.Destinations
import com.github.fsanaulla.utils.constants.Destinations.Destination

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
private[fsanaulla] trait SubscriptionManagement[R, U, M, E] extends SubscriptionsManagementQuery[U] {
  self: RequestHandler[R, U, M, E]
    with ResponseHandler[R]
    with QueryHandler[U]
    with HasCredentials =>

  protected implicit val ex: ExecutionContext

  def createSubscription(subsName: String,
                         dbName: String,
                         rpName: String = "autogen",
                         destinationType: Destination,
                         addresses: Seq[String]): Future[Result] = {

    buildRequest(createSubscriptionQuery(subsName, dbName, rpName, destinationType, addresses))
      .flatMap(toResult)
  }

  def dropSubscription(subName: String,
                       dbName: String,
                       rpName: String): Future[Result] = {

    buildRequest(dropSubscriptionQuery(subName, dbName, rpName)).flatMap(toResult)
  }

  def showSubscriptionsInfo(): Future[QueryResult[SubscriptionInfo]] = {
    buildRequest(showSubscriptionsQuery()).flatMap(toSubscriptionQueryResult)
  }

  def showSubscriptions(dbName: String): Future[QueryResult[Subscription]] = {

    showSubscriptionsInfo().map { queryRes =>
      val seq = queryRes.queryResult.find(_.dbName == dbName).map(_.subscriptions).getOrElse(Nil)

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

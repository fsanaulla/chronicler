package com.fsanaulla.api

import com.fsanaulla.InfluxClient
import com.fsanaulla.model.InfluxImplicits._
import com.fsanaulla.model.{QueryResult, Result, SubscriptionInfo}
import com.fsanaulla.query.SubscriptionsManagementQuery
import com.fsanaulla.utils.ResponseHandler._
import com.fsanaulla.utils.constants.Destinations.Destination

import scala.concurrent.Future

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
private[fsanaulla] trait SubscriptionManagement extends SubscriptionsManagementQuery { self: InfluxClient =>

  def createSubscription(subsName: String,
                         dbName: String,
                         destinationType: Destination,
                         addresses: Seq[String],
                         rpName: String = "autogen"): Future[Result] = {
    buildRequest(createSubscriptionQuery(subsName, dbName, rpName, destinationType, addresses)).flatMap(toResult)
  }

  def dropSubscription(subsName: String, dbName: String, rpName: String): Future[Result] = {
    buildRequest(dropSubscriptionQuery(subsName, dbName, rpName)).flatMap(toResult)
  }

  def showSubscriptions(): Future[QueryResult[SubscriptionInfo]] = {
    buildRequest(showSubscriptionsQuery()).flatMap(toSubscriptionQueryResult)
  }

}

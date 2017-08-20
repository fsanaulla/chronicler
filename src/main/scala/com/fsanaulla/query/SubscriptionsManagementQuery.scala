package com.fsanaulla.query

import akka.http.scaladsl.model.Uri
import com.fsanaulla.model.InfluxCredentials
import com.fsanaulla.utils.QueryBuilder
import com.fsanaulla.utils.constants.Destinations.Destination

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
private[fsanaulla] trait SubscriptionsManagementQuery extends QueryBuilder {

  protected def createSubscriptionQuery(subsName: String,
                                   dbName: String,
                                   rpName: String,
                                   destinationType: Destination,
                                   addresses: Seq[String])(implicit credentials: InfluxCredentials): Uri = {
    val addressesStr = addresses.map(str => s"\'$str\'").mkString(", ")
    buildQuery("/query", buildQueryParams(s"CREATE SUBSCRIPTION $subsName ON $dbName.$rpName DESTINATIONS $destinationType $addressesStr"))
  }

  protected def dropSubscriptionQuery(subsName: String, dbName: String, rpName: String)(implicit credentials: InfluxCredentials): Uri = {
    buildQuery("/query", buildQueryParams(s"DROP SUBSCRIPTION $subsName ON $dbName.$rpName"))
  }

  protected def showSubscriptionsQuery()(implicit credentials: InfluxCredentials): Uri = {
    buildQuery("/query", buildQueryParams("SHOW SUBSCRIPTIONS"))
  }
}

package com.github.fsanaulla.query

import com.github.fsanaulla.handlers.QueryHandler
import com.github.fsanaulla.model.InfluxCredentials
import com.github.fsanaulla.utils.constants.Destinations.Destination

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
private[fsanaulla] trait SubscriptionsManagementQuery[U] {
  self: QueryHandler[U] =>

  protected def createSubscriptionQuery(subsName: String,
                                        dbName: String,
                                        rpName: String,
                                        destinationType: Destination,
                                        addresses: Seq[String])
                                       (implicit credentials: InfluxCredentials): U = {

    val addressesStr = addresses.map(str => s"\'$str\'").mkString(", ")
    buildQuery("/query", buildQueryParams(s"CREATE SUBSCRIPTION $subsName ON $dbName.$rpName DESTINATIONS $destinationType $addressesStr"))
  }

  protected def dropSubscriptionQuery(subsName: String,
                                      dbName: String,
                                      rpName: String)
                                     (implicit credentials: InfluxCredentials): U = {
    buildQuery("/query", buildQueryParams(s"DROP SUBSCRIPTION $subsName ON $dbName.$rpName"))
  }

  protected def showSubscriptionsQuery()(implicit credentials: InfluxCredentials): U = {
    buildQuery("/query", buildQueryParams("SHOW SUBSCRIPTIONS"))
  }
}

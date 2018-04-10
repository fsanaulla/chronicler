package com.github.fsanaulla.core.query

import com.github.fsanaulla.core.enums.Destination
import com.github.fsanaulla.core.handlers.query.QueryHandler
import com.github.fsanaulla.core.model.HasCredentials

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
private[fsanaulla] trait SubscriptionsManagementQuery[U] {
  self: QueryHandler[U] with HasCredentials =>

  def createSubscriptionQuery(subsName: String,
                              dbName: String,
                              rpName: String,
                              destinationType: Destination,
                              addresses: Seq[String]): U = {

    val addressesStr = addresses.map(str => s"\'$str\'").mkString(", ")
    buildQuery("/query", buildQueryParams(s"CREATE SUBSCRIPTION $subsName ON $dbName.$rpName DESTINATIONS $destinationType $addressesStr"))
  }

  def dropSubscriptionQuery(subsName: String,
                                      dbName: String,
                                      rpName: String): U = {
    buildQuery("/query", buildQueryParams(s"DROP SUBSCRIPTION $subsName ON $dbName.$rpName"))
  }

  def showSubscriptionsQuery(): U = {
    buildQuery("/query", buildQueryParams("SHOW SUBSCRIPTIONS"))
  }
}

package com.github.fsanaulla.chronicler.core.query

import com.github.fsanaulla.chronicler.core.enums.Destination
import com.github.fsanaulla.chronicler.core.handlers.QueryHandler
import com.github.fsanaulla.chronicler.core.model.HasCredentials

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
private[chronicler] trait SubscriptionsManagementQuery[U] {
  self: QueryHandler[U] with HasCredentials =>

  final def createSubscriptionQuery(subsName: String,
                                    dbName: String,
                                    rpName: String,
                                    destinationType: Destination,
                                    addresses: Seq[String]): U = {

    val addressesStr = addresses.map(str => s"\'$str\'").mkString(", ")
    buildQuery("/query", buildQueryParams(s"CREATE SUBSCRIPTION $subsName ON $dbName.$rpName DESTINATIONS $destinationType $addressesStr"))
  }

  final def dropSubscriptionQuery(subsName: String,
                                  dbName: String,
                                  rpName: String): U =
    buildQuery("/query", buildQueryParams(s"DROP SUBSCRIPTION $subsName ON $dbName.$rpName"))


  final def showSubscriptionsQuery(): U =
    buildQuery("/query", buildQueryParams("SHOW SUBSCRIPTIONS"))

}

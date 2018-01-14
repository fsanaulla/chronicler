package com.github.fsanaulla.query

import com.github.fsanaulla.handlers.QueryHandler
import com.github.fsanaulla.model.InfluxCredentials

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 08.08.17
  */
private[fsanaulla] trait ContinuousQuerys[U] {
  self: QueryHandler[U] =>

  protected def showCQQuery()(implicit credentials: InfluxCredentials): U =
    buildQuery("/query", buildQueryParams("SHOW CONTINUOUS QUERIES"))

  protected def dropCQQuery(dbName: String, cqName: String)(implicit credentials: InfluxCredentials): U = {
    buildQuery("/query", buildQueryParams(s"DROP CONTINUOUS QUERY $cqName ON $dbName"))
  }

  protected def createCQQuery(dbName: String,
                              cqName: String,
                              query: String)(implicit credentials: InfluxCredentials): U = {
    buildQuery("/query", buildQueryParams(s"CREATE CONTINUOUS QUERY $cqName ON $dbName BEGIN $query END"))
  }
}

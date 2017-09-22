package com.github.fsanaulla.query

import akka.http.scaladsl.model.Uri
import com.github.fsanaulla.model.InfluxCredentials
import com.github.fsanaulla.utils.QueryBuilder

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 08.08.17
  */
private[fsanaulla] trait ContinuousQuerys extends QueryBuilder {

  protected def showCQQuery()(implicit credentials: InfluxCredentials): Uri =
    buildQuery("/query", buildQueryParams("SHOW CONTINUOUS QUERIES"))

  protected def dropCQQuery(dbName: String, cqName: String)(implicit credentials: InfluxCredentials): Uri = {
    buildQuery("/query", buildQueryParams(s"DROP CONTINUOUS QUERY $cqName ON $dbName"))
  }

  protected def createCQQuery(dbName: String, cqName: String, query: String)(implicit credentials: InfluxCredentials): Uri = {
    buildQuery("/query", buildQueryParams(s"CREATE CONTINUOUS QUERY $cqName ON $dbName BEGIN $query END"))
  }
}

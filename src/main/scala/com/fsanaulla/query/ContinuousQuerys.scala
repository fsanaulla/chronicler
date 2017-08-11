package com.fsanaulla.query

import akka.http.scaladsl.model.Uri

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 08.08.17
  */
private[fsanaulla] trait ContinuousQuerys extends QueryBuilder {

  protected def showCQQuery(): Uri = queryBuilder("/query", "SHOW CONTINUOUS QUERIES")

  protected def dropCQQuery(dbName: String, cqName: String): Uri = {
    queryBuilder("/query", s"DROP CONTINUOUS QUERY $cqName ON $dbName")
  }

  protected def createCQQuery(dbName: String, cqName: String, query: String): Uri = {
    queryBuilder("/query", s"CREATE CONTINUOUS QUERY $cqName ON $dbName BEGIN $query END")
  }
}

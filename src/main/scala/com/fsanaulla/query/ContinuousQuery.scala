package com.fsanaulla.query

import akka.http.scaladsl.model.Uri

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 08.08.17
  */
private[fsanaulla] trait ContinuousQuery extends QueryBuilder {

  protected def showCQ(): Uri = queryBuilder("/query", "SHOW CONTINUOUS QUERIES")

  protected def deleteCQ(cqName: String, dbName: String): Uri = queryBuilder("/query", s"DROP CONTINUOUS QUERY $cqName ON $dbName")

  protected def createCQ(cqName: String, dbName: String, query: String): Uri = queryBuilder("/query", s"CREATE CONTINUOUS QUERY $cqName ON $dbName BEGIN $query END")
}

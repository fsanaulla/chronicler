package com.github.fsanaulla.chronicler.core.query

import com.github.fsanaulla.chronicler.core.handlers.QueryHandler
import com.github.fsanaulla.chronicler.core.model.HasCredentials

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 08.08.17
  */
private[chronicler] trait ContinuousQuerys[U] {
  self: QueryHandler[U] with HasCredentials =>

  private[chronicler] final def showCQQuery(): U =
    buildQuery("/query", buildQueryParams("SHOW CONTINUOUS QUERIES"))

  private[chronicler] final def dropCQQuery(dbName: String, cqName: String): U = {
    buildQuery("/query", buildQueryParams(s"DROP CONTINUOUS QUERY $cqName ON $dbName"))
  }

  private[chronicler] final def createCQQuery(dbName: String, cqName: String, query: String): U = {
    buildQuery("/query", buildQueryParams(s"CREATE CONTINUOUS QUERY $cqName ON $dbName BEGIN $query END"))
  }
}

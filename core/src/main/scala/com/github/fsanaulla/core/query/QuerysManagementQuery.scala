package com.github.fsanaulla.core.query

import com.github.fsanaulla.core.handlers.QueryHandler
import com.github.fsanaulla.core.model.HasCredentials

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
private[fsanaulla] trait QuerysManagementQuery[U] {
  self: QueryHandler[U] with HasCredentials =>

  def showQuerysQuery(): U = {
    buildQuery("/query", buildQueryParams("SHOW QUERIES"))
  }

  def killQueryQuery(queryId: Int): U = {
    buildQuery("/query", buildQueryParams(s"KILL QUERY $queryId"))
  }

}

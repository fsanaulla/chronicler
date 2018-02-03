package com.github.fsanaulla.core.query

import com.github.fsanaulla.core.handlers.QueryHandler
import com.github.fsanaulla.core.model.InfluxCredentials

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
private[fsanaulla] trait QuerysManagementQuery[U] {
  self: QueryHandler[U] =>

  protected def showQuerysQuery()(implicit credentials: InfluxCredentials): U = {
    buildQuery("/query", buildQueryParams("SHOW QUERIES"))
  }

  protected def killQueryQuery(queryId: Int)(implicit credentials: InfluxCredentials): U = {
    buildQuery("/query", buildQueryParams(s"KILL QUERY $queryId"))
  }

}

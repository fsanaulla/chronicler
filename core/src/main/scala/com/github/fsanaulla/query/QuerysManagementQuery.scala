package com.github.fsanaulla.query

import com.github.fsanaulla.handlers.QueryHandler
import com.github.fsanaulla.model.InfluxCredentials

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

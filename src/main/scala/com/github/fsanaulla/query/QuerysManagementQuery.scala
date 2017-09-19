package com.github.fsanaulla.query

import akka.http.scaladsl.model.Uri
import com.github.fsanaulla.model.InfluxCredentials
import com.github.fsanaulla.utils.QueryBuilder

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
private[fsanaulla] trait QuerysManagementQuery extends QueryBuilder {

  protected def showQuerysQuery()(implicit credentials: InfluxCredentials): Uri = {
    buildQuery("/query", buildQueryParams("SHOW QUERIES"))
  }

  protected def killQueryQuery(queryId: Int)(implicit credentials: InfluxCredentials): Uri = {
    buildQuery("/query", buildQueryParams(s"KILL QUERY $queryId"))
  }

}

package com.fsanaulla.query

import akka.http.scaladsl.model.Uri
import com.fsanaulla.model.InfluxCredentials

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

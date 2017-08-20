package com.fsanaulla.api

import com.fsanaulla.InfluxClient
import com.fsanaulla.model.InfluxImplicits._
import com.fsanaulla.model.{QueryInfo, QueryResult, Result}
import com.fsanaulla.query.QuerysManagementQuery
import com.fsanaulla.utils.ResponseHandler.{toQueryResult, toResult}

import scala.concurrent.Future

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
private[fsanaulla] trait QuerysManagement extends QuerysManagementQuery { self: InfluxClient =>

  def showQueries(): Future[QueryResult[QueryInfo]] = {
    buildRequest(showQuerysQuery()).flatMap(toQueryResult[QueryInfo])
  }

  def killQuery(queryId: Int): Future[Result] = {
    buildRequest(killQueryQuery(queryId)).flatMap(toResult)
  }
}

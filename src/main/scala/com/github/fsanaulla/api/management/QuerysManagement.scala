package com.github.fsanaulla.api.management

import com.github.fsanaulla.clients.InfluxHttpClient
import com.github.fsanaulla.model.InfluxImplicits._
import com.github.fsanaulla.model.{QueryInfo, QueryResult, Result}
import com.github.fsanaulla.query.QuerysManagementQuery
import com.github.fsanaulla.utils.ResponseHandler._

import scala.concurrent.Future

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
private[fsanaulla] trait QuerysManagement extends QuerysManagementQuery {
  self: InfluxHttpClient =>

  def showQueries(): Future[QueryResult[QueryInfo]] = {
    buildRequest(showQuerysQuery()).flatMap(toQueryResult[QueryInfo])
  }

  def killQuery(queryId: Int): Future[Result] = {
    buildRequest(killQueryQuery(queryId)).flatMap(toResult)
  }
}

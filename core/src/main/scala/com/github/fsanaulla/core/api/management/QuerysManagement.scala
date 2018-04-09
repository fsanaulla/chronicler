package com.github.fsanaulla.core.api.management

import com.github.fsanaulla.core.handlers.RequestHandler
import com.github.fsanaulla.core.handlers.query.QueryHandler
import com.github.fsanaulla.core.handlers.response.ResponseHandler
import com.github.fsanaulla.core.model._
import com.github.fsanaulla.core.query.QuerysManagementQuery
import com.github.fsanaulla.core.utils.DefaultInfluxImplicits._

import scala.concurrent.Future

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
private[fsanaulla] trait QuerysManagement[R, U, M, E] extends QuerysManagementQuery[U] {
  self: RequestHandler[R, U, M, E]
    with ResponseHandler[R]
    with QueryHandler[U]
    with HasCredentials
    with Executable =>

  def showQueries(): Future[QueryResult[QueryInfo]] = {
    readRequest(showQuerysQuery()).flatMap(toQueryResult[QueryInfo])
  }

  def killQuery(queryId: Int): Future[Result] = {
    readRequest(killQueryQuery(queryId)).flatMap(toResult)
  }
}

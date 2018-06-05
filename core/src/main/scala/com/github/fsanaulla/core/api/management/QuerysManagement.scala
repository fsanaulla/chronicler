package com.github.fsanaulla.core.api.management

import com.github.fsanaulla.core.handlers.{QueryHandler, RequestHandler, ResponseHandler}
import com.github.fsanaulla.core.model._
import com.github.fsanaulla.core.query.QuerysManagementQuery
import com.github.fsanaulla.core.utils.DefaultInfluxImplicits._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
private[fsanaulla] trait QuerysManagement[M[_], R, U, E] extends QuerysManagementQuery[U] {
  self: RequestHandler[M, R, U, E]
    with ResponseHandler[M, R]
    with QueryHandler[U]
    with Mappable[M, R]
    with HasCredentials =>

  /** Show list of queries */
  final def showQueries: M[QueryResult[QueryInfo]] =
    m.mapTo(readRequest(showQuerysQuery()), toQueryResult[QueryInfo])


  /** Kill query */
  final def killQuery(queryId: Int): M[Result] =
    m.mapTo(readRequest(killQueryQuery(queryId)), toResult)

}

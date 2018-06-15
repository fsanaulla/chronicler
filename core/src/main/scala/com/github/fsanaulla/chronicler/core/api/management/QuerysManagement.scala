package com.github.fsanaulla.chronicler.core.api.management

import com.github.fsanaulla.chronicler.core.handlers.{QueryHandler, RequestHandler, ResponseHandler}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
private[chronicler] trait QuerysManagement[M[_], R, U, E] extends QuerysManagementQuery[U] {
  self: RequestHandler[M, R, U, E]
    with ResponseHandler[M, R]
    with QueryHandler[U]
    with Mappable[M, R]
    with HasCredentials =>

  /** Show list of queries */
  final def showQueries: M[QueryResult[QueryInfo]] =
    m.mapTo(readRequest(showQuerysQuery()), toQueryResult[QueryInfo])


  /** Kill query */
  final def killQuery(queryId: Int): M[WriteResult] =
    m.mapTo(readRequest(killQueryQuery(queryId)), toResult)

}

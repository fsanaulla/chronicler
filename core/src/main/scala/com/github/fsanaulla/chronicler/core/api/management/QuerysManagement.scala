package com.github.fsanaulla.chronicler.core.api.management

import com.github.fsanaulla.chronicler.core.handlers.{QueryHandler, RequestHandler, ResponseHandler}
import com.github.fsanaulla.chronicler.core.model._
import com.github.fsanaulla.chronicler.core.query.QuerysManagementQuery
import com.github.fsanaulla.chronicler.core.utils.DefaultInfluxImplicits._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
private[chronicler] trait QuerysManagement[M[_], Req, Resp, Uri, Entity] extends QuerysManagementQuery[Uri] {
  self: RequestHandler[M, Req, Resp, Uri]
    with ResponseHandler[M, Resp]
    with QueryHandler[Uri]
    with Mappable[M, Resp]
    with ImplicitRequestBuilder[Uri, Req]
    with HasCredentials =>

  /** Show list of queries */
  final def showQueries: M[QueryResult[QueryInfo]] =
    mapTo(execute(showQuerysQuery()), toQueryResult[QueryInfo])


  /** Kill query */
  final def killQuery(queryId: Int): M[WriteResult] =
    mapTo(execute(killQueryQuery(queryId)), toResult)

}

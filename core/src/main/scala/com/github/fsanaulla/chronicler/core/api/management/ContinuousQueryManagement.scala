package com.github.fsanaulla.chronicler.core.api.management

import com.github.fsanaulla.chronicler.core.handlers.{QueryHandler, RequestHandler, ResponseHandler}
import com.github.fsanaulla.chronicler.core.model._
import com.github.fsanaulla.chronicler.core.query.ContinuousQuerys
import com.github.fsanaulla.chronicler.core.utils.DefaultInfluxImplicits._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 08.08.17
  */
private[chronicler] trait ContinuousQueryManagement[M[_], Req, Resp, Uri, Entity] extends ContinuousQuerys[Uri] {
  self: RequestHandler[M, Req, Resp]
    with ResponseHandler[M, Resp]
    with QueryHandler[Uri]
    with Mappable[M, Resp]
    with RequestBuilder[Uri, Req]
    with HasCredentials =>

  /**
    * Create new one continuous query
    * @param dbName - database on which CQ will runes
    * @param cqName - continuous query name
    * @param query  - query
    * @return
    */
  final def createCQ(dbName: String, cqName: String, query: String): M[WriteResult] = {
    require(validCQQuery(query), "Query required INTO and GROUP BY clause")
    mapTo(execute(createCQQuery(dbName, cqName, query)), toResult)
  }

  /** Show continuous query information */
  final def showCQs: M[QueryResult[ContinuousQueryInfo]] =
    mapTo(execute(showCQQuery()), toCqQueryResult)

  /**
    * Drop continuous query
    * @param dbName - database name
    * @param cqName - continuous query name
    * @return       - execution result
    */
  final def dropCQ(dbName: String, cqName: String): M[WriteResult] =
    mapTo(execute(dropCQQuery(dbName, cqName)), toResult)

  private def validCQQuery(query: String): Boolean =
    if (query.contains("INTO") && query.contains("GROUP BY")) true else false

}

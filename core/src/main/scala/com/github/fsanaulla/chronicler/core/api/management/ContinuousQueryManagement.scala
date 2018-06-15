package com.github.fsanaulla.chronicler.core.api.management

import com.github.fsanaulla.chronicler.core.handlers.{QueryHandler, RequestHandler, ResponseHandler}
import com.github.fsanaulla.chronicler.core.model._
import com.github.fsanaulla.chronicler.core.query.ContinuousQuerys


/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 08.08.17
  */
private[chronicler] trait ContinuousQueryManagement[M[_], R, U, E] extends ContinuousQuerys[U] {
  self: RequestHandler[M, R, U, E]
    with ResponseHandler[M, R]
    with QueryHandler[U]
    with Mappable[M, R]
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
    m.mapTo(readRequest(uri = createCQQuery(dbName, cqName, query)), toResult)
  }

  /** Show continuous query information */
  final def showCQs: M[QueryResult[ContinuousQueryInfo]] =
    m.mapTo(readRequest(uri = showCQQuery()), toCqQueryResult)

//  def showCQ(dbName: String): M[QueryResult[ContinuousQuery]] = {
//    showCQs()
//      .map(_.queryResult)
//      .map(_.find(_.dbName == dbName))
//      .map {
//        case Some(cqi) => cqi.querys
//        case _ => Array.empty[ContinuousQuery]
//      }
//      .map(seq => QueryResult[ContinuousQuery](200, isSuccess = true, seq))
//  }

  /**
    * Drop continuous query
    * @param dbName - database name
    * @param cqName - continuous query name
    * @return       - execution result
    */
  final def dropCQ(dbName: String, cqName: String): M[WriteResult] =
    m.mapTo(readRequest(uri = dropCQQuery(dbName, cqName)), toResult)

//  def updateCQ(dbName: String, cqName: String, query: String): M[Result] = {
//    for {
//      dropRes <- dropCQ(dbName, cqName) if dropRes.code == 200
//      createRes <- createCQ(dbName, cqName, query)
//    } yield createRes
//  }

  private def validCQQuery(query: String): Boolean = {
    if (query.contains("INTO") && query.contains("GROUP BY")) true else false
  }
}

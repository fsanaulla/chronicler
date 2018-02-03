package com.github.fsanaulla.core.api.management

import com.github.fsanaulla.core.handlers.{QueryHandler, RequestHandler, ResponseHandler}
import com.github.fsanaulla.core.model.InfluxImplicits._
import com.github.fsanaulla.core.model._
import com.github.fsanaulla.core.query.ContinuousQuerys

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 08.08.17
  */
private[fsanaulla] trait ContinuousQueryManagement[R, U, M, E] extends ContinuousQuerys[U] {
  self: RequestHandler[R, U, M, E]
    with ResponseHandler[R]
    with QueryHandler[U]
    with HasCredentials =>

  protected implicit val ex: ExecutionContext

  def showCQs(): Future[QueryResult[ContinuousQueryInfo]] = {
    buildRequest(uri = showCQQuery())
      .flatMap(toCqQueryResult)
  }

  def showCQ(dbName: String): Future[QueryResult[ContinuousQuery]] = {
    showCQs()
      .map(_.queryResult)
      .map(_.find(_.dbName == dbName))
      .map {
        case Some(cqi) => cqi.querys
        case _ => Nil
      }
      .map(seq => QueryResult[ContinuousQuery](200, isSuccess = true, seq))
  }

  def dropCQ(dbName: String, cqName: String): Future[Result] = {
    buildRequest(uri = dropCQQuery(dbName, cqName)).flatMap(toResult)
  }

  def createCQ(dbName: String, cqName: String, query: String): Future[Result] = {
    require(validCQQuery(query), "Query required INTO and GROUP BY clause")
    buildRequest(uri = createCQQuery(dbName, cqName, query)).flatMap(toResult)
  }

  def updateCQ(dbName: String, cqName: String, query: String): Future[Result] = {
    for {
      dropRes <- dropCQ(dbName, cqName) if dropRes.code == 200
      createRes <- createCQ(dbName, cqName, query)
    } yield createRes
  }

  private def validCQQuery(query: String): Boolean = {
    if (query.contains("INTO") && query.contains("GROUP BY")) true else false
  }
}

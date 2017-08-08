package com.fsanaulla.api

import com.fsanaulla.InfluxClient
import com.fsanaulla.model.{ContinuousQueryInfo, QueryResult, Result}
import com.fsanaulla.query.ContinuousQuery
import com.fsanaulla.utils.ResponseWrapper.{toQueryResult, toResult}

import scala.concurrent.Future

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 08.08.17
  */
private[fsanaulla] trait ContinuousQueryManagement extends ContinuousQuery { self: InfluxClient =>

  def showCQ(): Future[QueryResult[ContinuousQueryInfo]] = buildRequest(showCQQuery()).flatMap(toQueryResult[ContinuousQueryInfo])

  def deleteCQ(dbName: String, cqName: String): Future[Result] = buildRequest(deleteCQQuery(cqName, dbName)).flatMap(toResult)

  def createCQ(dbName: String, cqName: String, query: String): Future[Result] = buildRequest(createCQQuery(dbName, cqName, query)).flatMap(toResult)

}

package com.github.fsanaulla.chronicler.async.io

import com.github.fsanaulla.chronicler.async.handlers.{AsyncQueryHandler, AsyncRequestHandler, AsyncResponseHandler}
import com.github.fsanaulla.chronicler.core.enums.Epoch
import com.github.fsanaulla.chronicler.core.io.ReadOperations
import com.github.fsanaulla.chronicler.core.model.{HasCredentials, QueryResult, ReadResult}
import com.github.fsanaulla.chronicler.core.query.DatabaseOperationQuery
import com.softwaremill.sttp.Uri
import jawn.ast.JArray

import scala.concurrent.Future

private[fsanaulla] trait AsyncReader
  extends AsyncQueryHandler
    with AsyncRequestHandler
    with AsyncResponseHandler
    with DatabaseOperationQuery[Uri]
    with HasCredentials { self: ReadOperations[Future] =>

  override def readJs0(dbName: String,
                       query: String,
                       epoch: Epoch,
                       pretty: Boolean,
                       chunked: Boolean): Future[ReadResult[JArray]] = {
    val executionResult = readRequest(readFromInfluxSingleQuery(dbName, query, epoch, pretty, chunked))
    query match {
      case q: String if q.contains("GROUP BY") => executionResult.flatMap(toGroupedJsResult)
      case _ => executionResult.flatMap(toQueryJsResult)
    }
  }

  override def bulkReadJs0(dbName: String,
                           queries: Seq[String],
                           epoch: Epoch,
                           pretty: Boolean,
                           chunked: Boolean): Future[QueryResult[Array[JArray]]] = {
    val query = readFromInfluxBulkQuery(dbName, queries, epoch, pretty, chunked)
    readRequest(query).flatMap(toBulkQueryJsResult)
  }
}

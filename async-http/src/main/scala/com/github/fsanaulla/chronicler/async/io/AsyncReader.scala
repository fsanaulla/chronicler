package com.github.fsanaulla.chronicler.async.io

import com.github.fsanaulla.chronicler.async.handlers.{AsyncQueryHandler, AsyncRequestHandler, AsyncResponseHandler}
import com.github.fsanaulla.core.enums.Epoch
import com.github.fsanaulla.core.io.ReadOperations
import com.github.fsanaulla.core.model.{HasCredentials, InfluxCredentials, QueryResult}
import com.github.fsanaulla.core.query.DatabaseOperationQuery
import com.softwaremill.sttp.{Method, Uri}
import spray.json.JsArray

import scala.concurrent.Future

private[fsanaulla] trait AsyncReader
  extends AsyncQueryHandler
    with AsyncRequestHandler
    with AsyncResponseHandler
    with DatabaseOperationQuery[Uri]
    with HasCredentials { self: ReadOperations =>

  protected override def _readJs(dbName: String,
                                 query: String,
                                 epoch: Epoch,
                                 pretty: Boolean,
                                 chunked: Boolean): Future[QueryResult[JsArray]] = {
    val _query = readFromInfluxSingleQuery(dbName, query, epoch, pretty, chunked)
    readRequest(_query, Method.GET).flatMap(toQueryJsResult)
  }

  protected override def _bulkReadJs(dbName: String,
                                     queries: Seq[String],
                                     epoch: Epoch,
                                     pretty: Boolean,
                                     chunked: Boolean): Future[QueryResult[Seq[JsArray]]] = {
    val query = readFromInfluxBulkQuery(dbName, queries, epoch, pretty, chunked)
    readRequest(query, Method.GET).flatMap(toBulkQueryJsResult)
  }


}

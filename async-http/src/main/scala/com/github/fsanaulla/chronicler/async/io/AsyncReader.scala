package com.github.fsanaulla.chronicler.async.io

import com.github.fsanaulla.chronicler.async.handlers.{AsyncQueryHandler, AsyncRequestHandler, AsyncResponseHandler}
import com.github.fsanaulla.core.io.ReadOperations
import com.github.fsanaulla.core.model.{HasCredentials, InfluxCredentials, QueryResult}
import com.github.fsanaulla.core.query.DatabaseOperationQuery
import com.github.fsanaulla.core.utils.constants.Epochs
import com.github.fsanaulla.core.utils.constants.Epochs.Epoch
import com.softwaremill.sttp.{Method, Uri}
import spray.json.JsArray

import scala.concurrent.Future

private[fsanaulla] trait AsyncReader
  extends AsyncQueryHandler
    with AsyncRequestHandler
    with AsyncResponseHandler
    with DatabaseOperationQuery[Uri]
    with HasCredentials { self: ReadOperations =>

  override def _readJs(dbName: String,
                       query: String,
                       epoch: Epoch = Epochs.NANOSECONDS,
                       pretty: Boolean = false,
                       chunked: Boolean = false): Future[QueryResult[JsArray]] = {
    val _query = readFromInfluxSingleQuery(dbName, query, epoch, pretty, chunked)
    readRequest(_query, Method.GET).flatMap(toQueryJsResult)
  }

  override def _bulkReadJs(dbName: String,
                           queries: Seq[String],
                           epoch: Epoch = Epochs.NANOSECONDS,
                           pretty: Boolean = false,
                           chunked: Boolean = false): Future[QueryResult[Seq[JsArray]]] = {
    val query = readFromInfluxBulkQuery(dbName, queries, epoch, pretty, chunked)
    readRequest(query, Method.GET).flatMap(toBulkQueryJsResult)
  }


}

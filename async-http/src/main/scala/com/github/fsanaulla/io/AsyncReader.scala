package com.github.fsanaulla.io

import com.github.fsanaulla.handlers.{AsyncQueryHandler, AsyncRequestHandler, AsyncResponseHandler}
import com.github.fsanaulla.model.QueryResult
import com.github.fsanaulla.query.DatabaseOperationQuery
import com.github.fsanaulla.utils.constants.Epochs
import com.github.fsanaulla.utils.constants.Epochs.Epoch
import com.softwaremill.sttp.{Method, Uri}
import spray.json.JsArray

import scala.concurrent.Future

private[fsanaulla] trait AsyncReader
  extends AsyncQueryHandler
    with AsyncRequestHandler
    with AsyncResponseHandler
    with DatabaseOperationQuery[Uri] { self: ReadOperations =>

  override def readJs0(dbName: String,
                       query: String,
                       epoch: Epoch = Epochs.NANOSECONDS,
                       pretty: Boolean = false,
                       chunked: Boolean = false): Future[QueryResult[JsArray]] = {

    readRequest(readFromInfluxSingleQuery(dbName, query, epoch, pretty, chunked), Method.GET)
      .flatMap(toQueryJsResult)
  }

  override def bulkReadJs0(dbName: String,
                           querys: Seq[String],
                           epoch: Epoch = Epochs.NANOSECONDS,
                           pretty: Boolean = false,
                           chunked: Boolean = false): Future[QueryResult[Seq[JsArray]]] = {
    readRequest(readFromInfluxBulkQuery(dbName, querys, epoch, pretty, chunked), Method.GET)
      .flatMap(toBulkQueryJsResult)
  }


}

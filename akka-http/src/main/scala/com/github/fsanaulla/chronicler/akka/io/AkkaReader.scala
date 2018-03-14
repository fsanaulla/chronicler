package com.github.fsanaulla.chronicler.akka.io

import _root_.akka.http.scaladsl.model.HttpMethods.GET
import _root_.akka.http.scaladsl.model.Uri
import com.github.fsanaulla.chronicler.akka.handlers.{AkkaQueryHandler, AkkaRequestHandler, AkkaResponseHandler}
import com.github.fsanaulla.core.io.ReadOperations
import com.github.fsanaulla.core.model.{Executable, HasCredentials, InfluxCredentials, QueryResult}
import com.github.fsanaulla.core.query.DatabaseOperationQuery
import com.github.fsanaulla.core.utils.constants.Epochs
import com.github.fsanaulla.core.utils.constants.Epochs.Epoch
import spray.json.JsArray

import scala.concurrent.Future

private[fsanaulla] trait AkkaReader
  extends AkkaRequestHandler
    with AkkaResponseHandler
    with AkkaQueryHandler
    with DatabaseOperationQuery[Uri] {
  self: ReadOperations with HasCredentials with Executable =>

  override def _readJs(dbName: String,
                       query: String,
                       epoch: Epoch = Epochs.NANOSECONDS,
                       pretty: Boolean = false,
                       chunked: Boolean = false): Future[QueryResult[JsArray]] = {

    readRequest(readFromInfluxSingleQuery(dbName, query, epoch, pretty, chunked), GET)
      .flatMap(toQueryJsResult)
  }

  override def _bulkReadJs(dbName: String,
                           querys: Seq[String],
                           epoch: Epoch = Epochs.NANOSECONDS,
                           pretty: Boolean = false,
                           chunked: Boolean = false): Future[QueryResult[Seq[JsArray]]] = {
    readRequest(readFromInfluxBulkQuery(dbName, querys, epoch, pretty, chunked), GET)
      .flatMap(toBulkQueryJsResult)
  }
}

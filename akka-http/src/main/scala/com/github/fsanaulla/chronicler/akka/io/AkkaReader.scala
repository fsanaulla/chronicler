package com.github.fsanaulla.chronicler.akka.io

import _root_.akka.http.scaladsl.model.HttpMethods.GET
import _root_.akka.http.scaladsl.model.Uri
import com.github.fsanaulla.chronicler.akka.handlers.{AkkaQueryHandler, AkkaRequestHandler, AkkaResponseHandler}
import com.github.fsanaulla.core.enums.Epoch
import com.github.fsanaulla.core.io.ReadOperations
import com.github.fsanaulla.core.model.{Executable, HasCredentials, InfluxCredentials, QueryResult}
import com.github.fsanaulla.core.query.DatabaseOperationQuery
import spray.json.JsArray

import scala.concurrent.Future

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 15.03.18
  */
private[fsanaulla] trait AkkaReader
  extends AkkaRequestHandler
    with AkkaResponseHandler
    with AkkaQueryHandler
    with DatabaseOperationQuery[Uri]
    with HasCredentials { self: ReadOperations  with Executable =>

  protected override def _readJs(dbName: String,
                       query: String,
                       epoch: Epoch,
                       pretty: Boolean,
                       chunked: Boolean): Future[QueryResult[JsArray]] = {

    readRequest(readFromInfluxSingleQuery(dbName, query, epoch, pretty, chunked), GET)
      .flatMap(toQueryJsResult)
  }

  protected override def _bulkReadJs(dbName: String,
                           queries: Seq[String],
                           epoch: Epoch,
                           pretty: Boolean,
                           chunked: Boolean): Future[QueryResult[Seq[JsArray]]] = {
    readRequest(readFromInfluxBulkQuery(dbName, queries, epoch, pretty, chunked), GET)
      .flatMap(toBulkQueryJsResult)
  }
}

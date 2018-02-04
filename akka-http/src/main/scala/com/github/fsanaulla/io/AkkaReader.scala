package com.github.fsanaulla.io

import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model.Uri
import com.github.fsanaulla.core.io.ReadOperations
import com.github.fsanaulla.core.model.{InfluxCredentials, QueryResult}
import com.github.fsanaulla.core.query.DatabaseOperationQuery
import com.github.fsanaulla.core.utils.constants.Epochs
import com.github.fsanaulla.core.utils.constants.Epochs.Epoch
import com.github.fsanaulla.handlers.{AkkaQueryHandler, AkkaRequestHandler, AkkaResponseHandler}
import spray.json.JsArray

import scala.concurrent.{ExecutionContext, Future}

private[fsanaulla] trait AkkaReader
  extends AkkaRequestHandler
    with AkkaResponseHandler
    with AkkaQueryHandler
    with DatabaseOperationQuery[Uri] { self: ReadOperations =>

  protected implicit val ex: ExecutionContext
  protected implicit val credentials: InfluxCredentials

  override def readJs0(dbName: String,
                       query: String,
                       epoch: Epoch = Epochs.NANOSECONDS,
                       pretty: Boolean = false,
                       chunked: Boolean = false): Future[QueryResult[JsArray]] = {

    readRequest(readFromInfluxSingleQuery(dbName, query, epoch, pretty, chunked), GET)
      .flatMap(toQueryJsResult)
  }

  override def bulkReadJs0(dbName: String,
                           querys: Seq[String],
                           epoch: Epoch = Epochs.NANOSECONDS,
                           pretty: Boolean = false,
                           chunked: Boolean = false): Future[QueryResult[Seq[JsArray]]] = {
    readRequest(readFromInfluxBulkQuery(dbName, querys, epoch, pretty, chunked), GET)
      .flatMap(toBulkQueryJsResult)
  }
}

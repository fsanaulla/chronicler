package com.github.fsanaulla.chronicler.urlhttp.io

import com.github.fsanaulla.chronicler.urlhttp.handlers.{UrlQueryHandler, UrlRequestHandler, UrlResponseHandler}
import com.github.fsanaulla.core.enums.Epoch
import com.github.fsanaulla.core.io.ReadOperations
import com.github.fsanaulla.core.model.{HasCredentials, QueryResult}
import com.github.fsanaulla.core.query.DatabaseOperationQuery
import com.softwaremill.sttp.Uri
import jawn.ast.JArray

import scala.util.Try

private[fsanaulla] trait UrlReader
  extends UrlQueryHandler
    with UrlRequestHandler
    with UrlResponseHandler
    with DatabaseOperationQuery[Uri]
    with HasCredentials { self: ReadOperations[Try] =>

  override def readJs0(dbName: String,
                       query: String,
                       epoch: Epoch,
                       pretty: Boolean,
                       chunked: Boolean): Try[QueryResult[JArray]] = {
    val _query = readFromInfluxSingleQuery(dbName, query, epoch, pretty, chunked)
    readRequest(_query).flatMap(toQueryJsResult)
  }

  override def bulkReadJs0(dbName: String,
                           queries: Seq[String],
                           epoch: Epoch,
                           pretty: Boolean,
                           chunked: Boolean): Try[QueryResult[Array[JArray]]] = {
    val query = readFromInfluxBulkQuery(dbName, queries, epoch, pretty, chunked)
    readRequest(query).flatMap(toBulkQueryJsResult)
  }
}

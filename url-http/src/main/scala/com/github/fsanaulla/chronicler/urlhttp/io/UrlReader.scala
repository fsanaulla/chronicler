package com.github.fsanaulla.chronicler.urlhttp.io

import com.github.fsanaulla.chronicler.core.enums.Epoch
import com.github.fsanaulla.chronicler.core.io.ReadOperations
import com.github.fsanaulla.chronicler.core.model.{HasCredentials, QueryResult, ReadResult}
import com.github.fsanaulla.chronicler.core.query.DatabaseOperationQuery
import com.github.fsanaulla.chronicler.urlhttp.handlers.{UrlQueryHandler, UrlRequestHandler, UrlResponseHandler}
import com.softwaremill.sttp.Uri
import jawn.ast.JArray

import scala.util.Try

private[urlhttp] trait UrlReader
  extends UrlQueryHandler
    with UrlRequestHandler
    with UrlResponseHandler
    with DatabaseOperationQuery[Uri]
    with HasCredentials { self: ReadOperations[Try] =>

  private[chronicler] override def readJs(dbName: String,
                      query: String,
                      epoch: Epoch,
                      pretty: Boolean,
                      chunked: Boolean): Try[ReadResult[JArray]] = {
    val executionResult = execute(readFromInfluxSingleQuery(dbName, query, epoch, pretty, chunked))

    query match {
      case q: String if q.contains("GROUP BY") => executionResult.flatMap(toGroupedJsResult)
      case _ => executionResult.flatMap(toQueryJsResult)
    }
  }

  private[chronicler] override def bulkReadJs(dbName: String,
                                           queries: Seq[String],
                                           epoch: Epoch,
                                           pretty: Boolean,
                                           chunked: Boolean): Try[QueryResult[Array[JArray]]] = {
    val query = readFromInfluxBulkQuery(dbName, queries, epoch, pretty, chunked)
    execute(query).flatMap(toBulkQueryJsResult)
  }
}

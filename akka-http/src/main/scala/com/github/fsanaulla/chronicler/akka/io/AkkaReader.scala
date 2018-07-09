package com.github.fsanaulla.chronicler.akka.io

import _root_.akka.http.scaladsl.model.Uri
import com.github.fsanaulla.chronicler.akka.handlers.{AkkaQueryHandler, AkkaRequestHandler, AkkaResponseHandler}
import com.github.fsanaulla.chronicler.core.enums.Epoch
import com.github.fsanaulla.chronicler.core.io.ReadOperations
import com.github.fsanaulla.chronicler.core.model._
import com.github.fsanaulla.chronicler.core.query.DatabaseOperationQuery
import jawn.ast.JArray

import scala.concurrent.Future

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 15.03.18
  */
private[akka] trait AkkaReader
  extends AkkaRequestHandler
    with AkkaResponseHandler
    with AkkaQueryHandler
    with DatabaseOperationQuery[Uri] {
  self: ReadOperations[Future] with Executable with HasCredentials =>

  override def readJs(dbName: String,
                      query: String,
                      epoch: Epoch,
                      pretty: Boolean,
                      chunked: Boolean): Future[ReadResult[JArray]] = {

    val uri = readFromInfluxSingleQuery(dbName, query, epoch, pretty, chunked)
    val executionResult = execute(uri)

    query match {
      case q: String if q.contains("GROUP BY") => executionResult.flatMap(toGroupedJsResult)
      case _ => executionResult.flatMap(toQueryJsResult)
    }
  }


  override def bulkReadJs(dbName: String,
                          queries: Seq[String],
                          epoch: Epoch,
                          pretty: Boolean,
                          chunked: Boolean): Future[QueryResult[Array[JArray]]] = {
    val uri = readFromInfluxBulkQuery(dbName, queries, epoch, pretty, chunked)

    execute(uri).flatMap(toBulkQueryJsResult)
  }
}

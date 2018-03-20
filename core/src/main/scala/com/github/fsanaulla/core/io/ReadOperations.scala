package com.github.fsanaulla.core.io

import com.github.fsanaulla.core.enums.{Epoch, Epochs}
import com.github.fsanaulla.core.model.QueryResult
import spray.json.JsArray

import scala.concurrent.Future

trait ReadOperations {

  def _readJs(dbName: String,
              query: String,
              epoch: Epoch = Epochs.NANOSECONDS,
              pretty: Boolean = false,
              chunked: Boolean = false): Future[QueryResult[JsArray]]

  def _bulkReadJs(dbName: String,
                  queries: Seq[String],
                  epoch: Epoch = Epochs.NANOSECONDS,
                  pretty: Boolean = false,
                  chunked: Boolean = false): Future[QueryResult[Seq[JsArray]]]

}

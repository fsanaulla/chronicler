package com.github.fsanaulla.api

import com.github.fsanaulla.model.QueryResult
import com.github.fsanaulla.utils.constants.Epochs
import com.github.fsanaulla.utils.constants.Epochs.Epoch
import spray.json.JsArray

import scala.concurrent.Future

trait ReadOperations {

  def readJs(dbName: String,
             query: String,
             epoch: Epoch = Epochs.NANOSECONDS,
             pretty: Boolean = false,
             chunked: Boolean = false): Future[QueryResult[JsArray]]

  def bulkReadJs(dbName: String,
                 querys: Seq[String],
                 epoch: Epoch = Epochs.NANOSECONDS,
                 pretty: Boolean = false,
                 chunked: Boolean = false): Future[QueryResult[Seq[JsArray]]]

}

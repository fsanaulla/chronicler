package com.github.fsanaulla.core.io

import com.github.fsanaulla.core.model.QueryResult
import com.github.fsanaulla.core.utils.constants.Epochs
import com.github.fsanaulla.core.utils.constants.Epochs.Epoch
import spray.json.JsArray

import scala.concurrent.Future

trait ReadOperations {

  def readJs0(dbName: String,
             query: String,
             epoch: Epoch = Epochs.NANOSECONDS,
             pretty: Boolean = false,
             chunked: Boolean = false): Future[QueryResult[JsArray]]

  def bulkReadJs0(dbName: String,
                  querys: Seq[String],
                  epoch: Epoch = Epochs.NANOSECONDS,
                  pretty: Boolean = false,
                  chunked: Boolean = false): Future[QueryResult[Seq[JsArray]]]

}
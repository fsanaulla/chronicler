package com.github.fsanaulla.core.query

import com.github.fsanaulla.core.handlers.QueryHandler
import com.github.fsanaulla.core.model.InfluxCredentials
import com.github.fsanaulla.core.utils.constants.Consistencys.Consistency
import com.github.fsanaulla.core.utils.constants.Epochs.Epoch
import com.github.fsanaulla.core.utils.constants.Precisions.Precision

import scala.collection.mutable

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.08.17
  */
private[fsanaulla] trait DatabaseOperationQuery[U] {
  self: QueryHandler[U] =>

  protected def writeToInfluxQuery(dbName: String,
                                   consistency: Consistency,
                                   precision: Precision,
                                   retentionPolicy: Option[String])
                                  (implicit credentials: InfluxCredentials): U = {

    val queryParams = scala.collection.mutable.Map[String, String](
      "db" -> dbName,
      "consistency" -> consistency,
      "precision" -> precision
    )

    for (rp <- retentionPolicy) {
      queryParams += ("rp" -> rp)
    }

    buildQuery("/write", buildQueryParams(queryParams))
  }

  protected def readFromInfluxSingleQuery(dbName: String,
                                          query: String,
                                          epoch: Epoch,
                                          pretty: Boolean,
                                          chunked: Boolean)
                                         (implicit credentials: InfluxCredentials): U = {

    val queryParams = scala.collection.mutable.Map[String, String](
      "db" -> dbName,
      "pretty" -> pretty.toString,
      "chunked" -> chunked.toString,
      "epoch" -> epoch,
      "q" -> query
    )

    buildQuery("/query", buildQueryParams(queryParams))
  }

  protected def readFromInfluxBulkQuery(dbName: String,
                                        querys: Seq[String],
                                        epoch: Epoch,
                                        pretty: Boolean,
                                        chunked: Boolean)
                                       (implicit credentials: InfluxCredentials): U = {

    val queryParams = mutable.Map[String, String](
      "db" -> dbName,
      "pretty" -> pretty.toString,
      "chunked" -> chunked.toString,
      "epoch" -> epoch,
      "q" -> querys.mkString(";")
    )

    buildQuery("/query", buildQueryParams(queryParams))
  }
}
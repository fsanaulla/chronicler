package com.github.fsanaulla.chronicler.core.query

import com.github.fsanaulla.chronicler.core.enums.{Consistency, Epoch, Precision}
import com.github.fsanaulla.chronicler.core.handlers.QueryHandler
import com.github.fsanaulla.chronicler.core.model.HasCredentials

import scala.collection.mutable

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.08.17
  */
private[chronicler] trait DatabaseOperationQuery[U] {
  self: QueryHandler[U] with HasCredentials =>

  final def writeToInfluxQuery(dbName: String,
                         consistency: Consistency,
                         precision: Precision,
                         retentionPolicy: Option[String]): U = {

    val queryParams = scala.collection.mutable.Map[String, String](
      "db" -> dbName,
      "consistency" -> consistency.toString,
      "precision" -> precision.toString
    )

    for (rp <- retentionPolicy) {
      queryParams += ("rp" -> rp)
    }

    buildQuery("/write", buildQueryParams(queryParams))
  }

  final def readFromInfluxSingleQuery(dbName: String,
                                          query: String,
                                          epoch: Epoch,
                                          pretty: Boolean,
                                          chunked: Boolean): U = {

    val queryParams = scala.collection.mutable.Map[String, String](
      "db" -> dbName,
      "pretty" -> pretty.toString,
      "chunked" -> chunked.toString,
      "epoch" -> epoch.toString,
      "q" -> query
    )

    buildQuery("/query", buildQueryParams(queryParams))
  }

  final def readFromInfluxBulkQuery(dbName: String,
                              queries: Seq[String],
                              epoch: Epoch,
                              pretty: Boolean,
                              chunked: Boolean): U = {
    val queryParams = mutable.Map[String, String](
      "db" -> dbName,
      "pretty" -> pretty.toString,
      "chunked" -> chunked.toString,
      "epoch" -> epoch.toString,
      "q" -> queries.mkString(";")
    )

    buildQuery("/query", buildQueryParams(queryParams))
  }
}

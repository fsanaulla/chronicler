package com.github.fsanaulla.query

// format: off
import akka.http.scaladsl.model.Uri
import com.github.fsanaulla.model.InfluxCredentials
import com.github.fsanaulla.utils.QueryBuilder
import com.github.fsanaulla.utils.constants.Consistencys._
import com.github.fsanaulla.utils.constants.Epochs._
import com.github.fsanaulla.utils.constants.Precisions._

import scala.collection.mutable

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.08.17
  */
private[fsanaulla] trait DatabaseOperationQuery extends QueryBuilder {

  protected def writeToInfluxQuery(dbName: String,
                                   consistency: Consistency,
                                   precision: Precision,
                                   retentionPolicy: Option[String])
                                  (implicit credentials: InfluxCredentials): Uri = {

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
                                         (implicit credentials: InfluxCredentials): Uri = {

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
                                       (implicit credentials: InfluxCredentials): Uri = {

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

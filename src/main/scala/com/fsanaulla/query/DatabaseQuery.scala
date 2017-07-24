package com.fsanaulla.query

import akka.http.scaladsl.model.Uri
import com.fsanaulla.utils.constants.{Consistency, Epoch, Precision}

/**
  * Created by fayaz on 04.07.17.
  */
trait DatabaseQuery extends QueryBuilder {

  protected def writeToInfluxQuery(dbName: String,
                         username: Option[String] = None,
                         password: Option[String] = None,
                         consistency: String = Consistency.ONE,
                         precision: String = Precision.NANOSECONDS,
                         retentionPolicy: Option[String] = None): Uri = {

    val queryParams = scala.collection.mutable.Map[String, String](
      "db" -> dbName,
      "consistency" -> consistency,
      "precision" -> precision
    )

    for (rp <- retentionPolicy) {
      queryParams += ("rp" -> rp)
    }

    for  {
      u <- username
      p <- password
    } yield queryParams += ("u" -> u, "p" -> p)


    queryBuilder("/write", queryParams.toMap)
  }

  protected def readFromInfluxSingleQuery(dbName: String,
                                query: String,
                                username: Option[String] = None,
                                password: Option[String] = None,
                                epoch: String = Epoch.NANOSECONDS,
                                pretty: Boolean = false,
                                chunked: Boolean = false
                          ): Uri = {

    val queryParams = scala.collection.mutable.Map[String, String](
      "db" -> dbName,
      "pretty" -> pretty.toString,
      "chunked" -> chunked.toString,
      "epoch" -> epoch,
      "q" -> query
    )

    for  {
      u <- username
      p <- password
    } yield queryParams += ("u" -> u, "p" -> p)


    queryBuilder("/query", queryParams.toMap)
  }

  protected def readFromInfluxBulkQuery(dbName: String,
                              querys: Seq[String],
                              username: Option[String] = None,
                              password: Option[String] = None,
                              epoch: String = Epoch.NANOSECONDS,
                              pretty: Boolean = false,
                              chunked: Boolean = false
                        ): Uri = {

    val queryParams = scala.collection.mutable.Map[String, String](
      "db" -> dbName,
      "pretty" -> pretty.toString,
      "chunked" -> chunked.toString,
      "epoch" -> epoch,
      "q" -> querys.mkString(";")
    )

    for  {
      u <- username
      p <- password
    } yield queryParams += ("u" -> u, "p" -> p)


    queryBuilder("/query", queryParams.toMap)
  }
}

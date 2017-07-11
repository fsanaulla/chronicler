package com.fsanaulla.query

import akka.http.scaladsl.model.Uri
import com.fsanaulla.utils.constants.{Consistency, Epoch, Precision}

/**
  * Created by fayaz on 04.07.17.
  */
trait DatabaseQuerys {

  def dropMeasurement(dbName: String,
                      measurementName: String): Uri = {
    val query = s"DROP SERIES FROM $measurementName"
    Uri("/query").withQuery(Uri.Query("db" -> dbName, "q" -> query))
  }

  def writeToDB(dbName: String,
                username: Option[String] = None,
                password: Option[String] = None,
                consistency: String = Consistency.ONE,
                precision: String = Precision.NANOSECONDS,
                retentionPolicy: Option[String] = None
               ): Uri = {

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


    Uri("/write").withQuery(Uri.Query(queryParams.toMap))
  }

  def readFromDB(dbName: String,
                 username: Option[String] = None,
                 password: Option[String] = None,
                 query: String,
                 epoch: String = Epoch.NANOSECONDS,
                 pretty: Boolean = false
                ): Uri = {

    val queryParams = scala.collection.mutable.Map[String, String](
      "db" -> dbName,
      "pretty" -> pretty.toString,
      "epoch" -> epoch,
      "q" -> query
    )

    for  {
      u <- username
      p <- password
    } yield queryParams += ("u" -> u, "p" -> p)


    Uri("/query").withQuery(Uri.Query(queryParams.toMap))
  }
}

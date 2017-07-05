package com.fsanaulla.query

import akka.http.scaladsl.model.Uri
import com.fsanaulla.utils.Constants.Consistency.Consistency
import com.fsanaulla.utils.Constants.Epoch.Epoch
import com.fsanaulla.utils.Constants.Precision.Precision
import com.fsanaulla.utils.Constants.{Consistency, Epoch, Precision}

/**
  * Created by fayaz on 04.07.17.
  */
trait DatabaseQuerys {

  def writeToDB(dbName: String,
                consistency: Consistency = Consistency.ONE,
                precision: Precision = Precision.NANOS,
                userName: Option[String] = None,
                password: Option[String] = None,
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
      u <- userName
      p <- password
    } yield queryParams += ("u" -> u, "p" -> p)


    Uri("/write").withQuery(Uri.Query(queryParams.toMap))
  }

  def readFromDB(dbName: String,
                 epoch: Epoch = Epoch.NANOS,
                 pretty: Boolean = false,
                 userName: Option[String] = None,
                 password: Option[String] = None
                ): Uri = {

    val queryParams = scala.collection.mutable.Map[String, String](
      "db" -> dbName,
      "pretty" -> pretty.toString,
      "epoch" -> epoch
    )

    for  {
      u <- userName
      p <- password
    } yield queryParams += ("u" -> u, "p" -> p)


    Uri("/query").withQuery(Uri.Query(queryParams.toMap))
  }
}

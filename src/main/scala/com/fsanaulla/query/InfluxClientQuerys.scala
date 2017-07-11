package com.fsanaulla.query

import akka.http.scaladsl.model.Uri

/**
  * Created by fayaz on 27.06.17.
  */
trait InfluxClientQuerys {

  def createDBQuery(dbName: String): Uri = {
    Uri("/query").withQuery(Uri.Query(Map("q" -> s"CREATE DATABASE $dbName")))
  }

  def dropDBQuery(dbName: String): Uri = {
    Uri("/query").withQuery(Uri.Query(Map("q" -> s"DROP DATABASE $dbName")))
  }
}

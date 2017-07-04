package com.fsanaulla.query

import akka.http.scaladsl.model.Uri

/**
  * Created by fayaz on 04.07.17.
  */
trait DatabaseQuerys {
  def writeToDB(dbName: String): Uri = Uri("/write").withQuery(Uri.Query(("db", dbName)))
}

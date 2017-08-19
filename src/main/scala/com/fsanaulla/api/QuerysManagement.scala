package com.fsanaulla.api

import akka.http.scaladsl.model.HttpResponse
import com.fsanaulla.InfluxClient
import com.fsanaulla.query.QuerysManagementQuery

import scala.concurrent.Future

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
private[fsanaulla] trait QuerysManagement extends QuerysManagementQuery { self: InfluxClient =>

  def showQueries(): Future[HttpResponse] = {
    buildRequest(showQuerysQuery())
  }
}

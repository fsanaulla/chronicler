package com.fsanaulla.utils

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.scaladsl.Flow

import scala.concurrent.Future

/**
  * Created by fayaz on 04.07.17.
  */
object TypeAlias {

  type ConnectionPoint = Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]]
}

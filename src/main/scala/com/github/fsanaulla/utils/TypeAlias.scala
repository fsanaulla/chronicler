package com.github.fsanaulla.utils

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.scaladsl.Flow
import spray.json.JsArray

import scala.concurrent.Future

/**
  * Created by fayaz on 04.07.17.
  */
private[fsanaulla] object TypeAlias {
  type Connection = Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]]
}

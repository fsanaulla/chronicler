package com.github.fsanaulla.chronicler.akka.utils

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.scaladsl.Flow

import scala.concurrent.Future

private[akka] object AkkaAlias {
  type Connection = Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]]
}

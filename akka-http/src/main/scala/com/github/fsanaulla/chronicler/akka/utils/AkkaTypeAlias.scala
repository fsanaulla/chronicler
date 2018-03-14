package com.github.fsanaulla.chronicler.akka.utils

import _root_.akka.http.scaladsl.Http
import _root_.akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import _root_.akka.stream.scaladsl.Flow

import scala.concurrent.Future

/**
  * Created by fayaz on 04.07.17.
  */
private[fsanaulla] object AkkaTypeAlias {

  type Connection = Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]]
}

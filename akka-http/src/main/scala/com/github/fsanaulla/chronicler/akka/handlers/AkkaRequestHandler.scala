package com.github.fsanaulla.chronicler.akka.handlers

import _root_.akka.http.scaladsl.model._
import _root_.akka.stream.ActorMaterializer
import _root_.akka.stream.scaladsl.{Sink, Source}
import com.github.fsanaulla.chronicler.akka.utils.AkkaAlias.Connection
import com.github.fsanaulla.chronicler.core.handlers.RequestHandler

import scala.concurrent.Future
import scala.language.implicitConversions

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 15.03.18
  */
private[akka] trait AkkaRequestHandler
  extends RequestHandler[Future, HttpRequest, HttpResponse, Uri] {

  protected implicit val mat: ActorMaterializer
  protected implicit val connection: Connection

  override implicit def req(uri: Uri): HttpRequest = HttpRequest(uri = uri)

  override def execute(request: HttpRequest): Future[HttpResponse] =
    Source.single(request).via(connection).runWith(Sink.head)
}

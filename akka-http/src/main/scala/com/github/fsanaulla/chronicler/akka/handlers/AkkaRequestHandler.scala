package com.github.fsanaulla.chronicler.akka.handlers

import _root_.akka.http.scaladsl.model._
import _root_.akka.stream.ActorMaterializer
import _root_.akka.stream.scaladsl.{Sink, Source}
import com.github.fsanaulla.chronicler.akka.utils.AkkaAlias.Connection
import com.github.fsanaulla.chronicler.core.handlers.RequestHandler

import scala.concurrent.Future

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 15.03.18
  */
private[akka] trait AkkaRequestHandler extends RequestHandler[Future, HttpRequest, HttpResponse] {

  protected implicit val mat: ActorMaterializer
  protected implicit val connection: Connection

  override def execute(request: HttpRequest): Future[HttpResponse] =
    Source.single(request).via(connection).runWith(Sink.head)
}

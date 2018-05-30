package com.github.fsanaulla.chronicler.akka.handlers

import _root_.akka.http.scaladsl.model.{MessageEntity, _}
import _root_.akka.stream.ActorMaterializer
import _root_.akka.stream.scaladsl.{Sink, Source}
import com.github.fsanaulla.chronicler.akka.utils.AkkaTypeAlias.Connection
import com.github.fsanaulla.core.handlers.RequestHandler

import scala.concurrent.Future

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 15.03.18
  */
private[fsanaulla] trait AkkaRequestHandler extends RequestHandler[Future, HttpResponse, Uri, MessageEntity] {

  protected implicit val mat: ActorMaterializer
  protected implicit val connection: Connection

  override def readRequest(uri: Uri, entity: Option[MessageEntity] = None): Future[HttpResponse] = {
    Source
      .single(
        HttpRequest(
          method = HttpMethods.GET,
          uri = uri,
          entity = entity.getOrElse(HttpEntity.Empty)
        )
      )
      .via(connection)
      .runWith(Sink.head)
  }

  override def writeRequest(uri: Uri, entity: MessageEntity): Future[HttpResponse] = {
    Source
      .single(
        HttpRequest(
          method = HttpMethods.POST,
          uri = uri,
          entity = entity
        )
      )
      .via(connection)
      .runWith(Sink.head)
  }
}

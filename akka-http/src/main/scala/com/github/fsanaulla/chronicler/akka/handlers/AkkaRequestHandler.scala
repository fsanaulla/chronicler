package com.github.fsanaulla.chronicler.akka.handlers

import _root_.akka.http.scaladsl.model._
import _root_.akka.stream.ActorMaterializer
import _root_.akka.stream.scaladsl.{Sink, Source}
import com.github.fsanaulla.chronicler.akka.utils.AkkaTypeAlias.Connection
import com.github.fsanaulla.core.handlers.RequestHandler

import scala.concurrent.Future

private[fsanaulla] trait AkkaRequestHandler
  extends RequestHandler[HttpResponse, Uri, HttpMethod, MessageEntity] {

  protected implicit val mat: ActorMaterializer
  protected implicit val connection: Connection
  protected val defaultMethod: HttpMethod = HttpMethods.POST

  override def readRequest(uri: Uri,
                           method: HttpMethod,
                           entity: Option[MessageEntity] = None): Future[HttpResponse] = {
    Source
      .single(
        HttpRequest(
          method = method,
          uri = uri,
          entity = entity.getOrElse(HttpEntity.Empty)
        )
      )
      .via(connection)
      .runWith(Sink.head)
  }

  override def writeRequest(uri: Uri,
                            method: HttpMethod = defaultMethod,
                            entity: MessageEntity): Future[HttpResponse] = {
    Source
      .single(
        HttpRequest(
          method = method,
          uri = uri,
          entity = entity
        )
      )
      .via(connection)
      .runWith(Sink.head)
  }
}

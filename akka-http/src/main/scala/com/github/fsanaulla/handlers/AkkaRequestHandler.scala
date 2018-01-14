package com.github.fsanaulla.handlers

import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.github.fsanaulla.utils.AkkaTypeAlias.Connection

import scala.concurrent.Future

private[fsanaulla] trait AkkaRequestHandler
  extends RequestHandler[HttpResponse, Uri, HttpMethod, RequestEntity] {

  protected implicit val mat: ActorMaterializer
  protected implicit val connection: Connection

  override val defaultMethod: HttpMethod = HttpMethods.POST
  override val defaultEntity: RequestEntity = HttpEntity.Empty

  def buildRequest(uri: Uri,
                   method: HttpMethod = HttpMethods.POST,
                   entity: RequestEntity = HttpEntity.Empty): Future[HttpResponse] = {
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

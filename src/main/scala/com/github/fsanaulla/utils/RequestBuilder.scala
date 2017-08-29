package com.github.fsanaulla.utils

import akka.http.scaladsl.model.HttpMethods.POST
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.github.fsanaulla.utils.TypeAlias.Connection

import scala.concurrent.Future

private[fsanaulla] trait RequestBuilder {
  protected def buildRequest(uri: Uri, method: HttpMethod = POST, entity: RequestEntity = HttpEntity.Empty)(implicit mat: ActorMaterializer, connection: Connection): Future[HttpResponse] = {
    Source.single(
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

package com.fsanaulla.api

import akka.http.scaladsl.model.HttpMethods.POST
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.fsanaulla.model.TypeAlias.ConnectionPoint

import scala.concurrent.Future

trait RequestBuilder {
  protected def buildRequest(uri: Uri, method: HttpMethod = POST, entity: RequestEntity = HttpEntity.Empty)(implicit mat: ActorMaterializer, connection: ConnectionPoint): Future[HttpResponse] = {
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

package com.fsanaulla.api

import akka.http.scaladsl.model.HttpMethods.POST
import akka.http.scaladsl.model.{HttpMethod, HttpRequest, HttpResponse, Uri}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.fsanaulla.model.TypeAlias.ConnectionPoint

import scala.concurrent.Future

trait RequestBuilder {
  protected def buildRequest(uri: Uri, method: HttpMethod = POST)(implicit mat: ActorMaterializer, connection: ConnectionPoint): Future[HttpResponse] = {
    Source.single(
      HttpRequest(
        method = method,
        uri = uri
      )
    )
      .via(connection)
      .runWith(Sink.head)
  }
}

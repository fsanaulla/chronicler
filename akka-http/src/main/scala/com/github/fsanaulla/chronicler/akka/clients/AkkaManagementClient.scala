package com.github.fsanaulla.chronicler.akka.clients

import _root_.akka.actor.ActorSystem
import _root_.akka.http.scaladsl.Http
import _root_.akka.http.scaladsl.model.{HttpRequest, HttpResponse, RequestEntity, Uri}
import _root_.akka.stream.{ActorMaterializer, StreamTcpException}
import com.github.fsanaulla.chronicler.akka.handlers.{AkkaQueryHandler, AkkaRequestHandler, AkkaResponseHandler}
import com.github.fsanaulla.chronicler.akka.utils.AkkaAlias.Connection
import com.github.fsanaulla.chronicler.core.client.ManagementClient
import com.github.fsanaulla.chronicler.core.model._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

final class AkkaManagementClient(host: String,
                                 port: Int,
                                 val credentials: Option[InfluxCredentials])
                                (implicit val ex: ExecutionContext, val system: ActorSystem)
  extends ManagementClient[Future, HttpRequest, HttpResponse, Uri, RequestEntity]
    with AkkaRequestHandler
    with AkkaResponseHandler
    with AkkaQueryHandler
    with HasCredentials
    with Mappable[Future, HttpResponse]
    with AutoCloseable {

  override def mapTo[B](resp: Future[HttpResponse],
                        f: HttpResponse => Future[B]): Future[B] = resp.flatMap(f)

  protected implicit val mat: ActorMaterializer = ActorMaterializer()
  protected implicit val connection: Connection = Http().outgoingConnection(host, port) recover {
    case ex: StreamTcpException => throw new ConnectionException(ex.getMessage)
    case unknown => throw new UnknownConnectionException(unknown.getMessage)
  }

  override def close(): Unit =
    Await.ready(Http().shutdownAllConnectionPools(), Duration.Inf)

  override def ping: Future[WriteResult] =
    mapTo(execute(Uri("/ping")), toResult)
}

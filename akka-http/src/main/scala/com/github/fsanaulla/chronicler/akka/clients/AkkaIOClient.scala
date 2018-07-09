package com.github.fsanaulla.chronicler.akka.clients

import _root_.akka.actor.ActorSystem
import _root_.akka.http.scaladsl.Http
import _root_.akka.http.scaladsl.model.RequestEntity
import _root_.akka.stream.{ActorMaterializer, StreamTcpException}
import com.github.fsanaulla.chronicler.akka.api.{Database, Measurement}
import com.github.fsanaulla.chronicler.akka.utils.AkkaAlias.Connection
import com.github.fsanaulla.chronicler.core.client.IOClient
import com.github.fsanaulla.chronicler.core.model.{ConnectionException, InfluxCredentials, UnknownConnectionException}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.reflect.ClassTag

final class AkkaIOClient(host: String,
                         port: Int,
                         val credentials: Option[InfluxCredentials],
                         gzipped: Boolean)
                        (implicit val ex: ExecutionContext, val system: ActorSystem)
  extends IOClient[Future, RequestEntity] with AutoCloseable {

  protected implicit val mat: ActorMaterializer = ActorMaterializer()
  protected implicit val connection: Connection = Http().outgoingConnection(host, port) recover {
    case ex: StreamTcpException => throw new ConnectionException(ex.getMessage)
    case unknown => throw new UnknownConnectionException(unknown.getMessage)
  }

  override def database(dbName: String): Database =
    new Database(dbName, credentials, gzipped)

  override def measurement[A: ClassTag](dbName: String,
                                        measurementName: String): Measurement[A] =
    new Measurement[A](dbName, measurementName, credentials, gzipped)

  override def close(): Unit =
    Await.ready(Http().shutdownAllConnectionPools(), Duration.Inf)
}

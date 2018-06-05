package com.github.fsanaulla.chronicler.akka

import _root_.akka.actor.ActorSystem
import _root_.akka.http.scaladsl.Http
import _root_.akka.http.scaladsl.model._
import _root_.akka.stream.{ActorMaterializer, StreamTcpException}
import com.github.fsanaulla.chronicler.akka.api.{Database, Measurement}
import com.github.fsanaulla.chronicler.akka.handlers.{AkkaQueryHandler, AkkaRequestHandler, AkkaResponseHandler}
import com.github.fsanaulla.chronicler.akka.utils.AkkaTypeAlias.Connection
import com.github.fsanaulla.core.client.InfluxClient
import com.github.fsanaulla.core.model._

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag
import scala.util.{Failure, Success}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.08.17
  */
final class InfluxAkkaHttpClient(
                                  host: String,
                                  port: Int,
                                  val credentials: Option[InfluxCredentials])
                                (implicit val ex: ExecutionContext, val system: ActorSystem)
    extends InfluxClient[Future, HttpResponse, Uri, RequestEntity]
      with AkkaRequestHandler
      with AkkaResponseHandler
      with AkkaQueryHandler {

  override val m: Mapper[Future, HttpResponse] = new Mapper[Future, HttpResponse] {
    override def mapTo[B](resp: Future[HttpResponse], f: HttpResponse => Future[B]): Future[B] = resp.flatMap(f)
  }

  protected implicit val mat: ActorMaterializer = ActorMaterializer()
  protected implicit val connection: Connection = Http().outgoingConnection(host, port) recover {
    case ex: StreamTcpException => throw new ConnectionException(ex.getMessage)
    case unknown => throw new UnknownConnectionException(unknown.getMessage)
  }

  /**
    *
    * @param dbName - database name
    * @return Database instance that provide non type safe operations
    */
  override def database(dbName: String): Database =
    new Database(dbName, credentials)

  /**
    *
    * @param dbName - database name
    * @param measurementName - measurement name
    * @tparam A - Measurement's time series type
    * @return - Measurement instance of type [A]
    */
  override def measurement[A: ClassTag](dbName: String, measurementName: String): Measurement[A] =
    new Measurement[A](dbName, measurementName, credentials)


  /**
    * Ping InfluxDB
    */
  override def ping: Future[Result] =
    m.mapTo(readRequest("/ping"), toResult)

  /**
    * Close HTTP connection
    */
  override def close(): Unit = {
    Http()
      .shutdownAllConnectionPools()
      .onComplete {
        case Success(_) =>
        case Failure(exc) => throw exc
      }
  }

  /**
    * Close HTTP connection  and  shut down actor system
    */
  def closeAll(): Unit = {
    close()
    system.terminate().onComplete {
      case Success(_) =>
      case Failure(exc) => throw exc
    }
  }
}

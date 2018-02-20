package com.github.fsanaulla.chronicler.akka

import _root_.akka.actor.ActorSystem
import _root_.akka.http.scaladsl.Http
import _root_.akka.http.scaladsl.model.HttpMethods.GET
import _root_.akka.http.scaladsl.model._
import _root_.akka.stream.{ActorMaterializer, StreamTcpException}
import com.github.fsanaulla.chronicler.akka.api.{Database, Measurement}
import com.github.fsanaulla.chronicler.akka.handlers.{AkkaQueryHandler, AkkaRequestHandler, AkkaResponseHandler}
import com.github.fsanaulla.chronicler.akka.utils.AkkaTypeAlias.Connection
import com.github.fsanaulla.core.client.InfluxClient
import com.github.fsanaulla.core.model._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.08.17
  */
private[fsanaulla] class InfluxAkkaHttpClient(host: String,
                                              port: Int,
                                              username: Option[String],
                                              password: Option[String])
                                             (implicit val ex: ExecutionContext, val system: ActorSystem)
    extends InfluxClient[HttpResponse, Uri, HttpMethod, RequestEntity]
      with AkkaRequestHandler
      with AkkaResponseHandler
      with AkkaQueryHandler {

  protected implicit val credentials: InfluxCredentials = InfluxCredentials(username, password)
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
  override def database(dbName: String): Database = new Database(dbName)

  /**
    *
    * @param dbName - database name
    * @param measurementName - measurement name
    * @tparam A - Measurement's time series type
    * @return - Measurement instance of type [A]
    */
  override def measurement[A](dbName: String, measurementName: String): Measurement[A] = {
    new Measurement[A](dbName, measurementName)
  }

  /**
    * Ping InfluxDB
    */
  override def ping(): Future[Result] = {
    readRequest("/ping", GET).flatMap(toResult)
  }

  /**
    * Close HTTP connection
    */
  override def close(): Unit = {
    Http()
      .shutdownAllConnectionPools()
      .onComplete {
        case Success(_) => println("Successfully stopped")
        case Failure(exc) => println(s"Failure when closing ${exc.getCause}")
      }
  }

  /**
    * Close HTTP connection  and  shut down actor system
    */
  def closeAll(): Unit = {
    close()
    system
      .terminate()
      .onComplete {
        case Success(_) => println("ActorSystem was successfully terminated")
        case Failure(exc) => println(s"Failure while closing ${exc.getCause}")
    }
  }
}

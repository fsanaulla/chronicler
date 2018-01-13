package com.github.fsanaulla.clients

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model.{HttpMethod, HttpResponse, RequestEntity, Uri}
import akka.stream.{ActorMaterializer, StreamTcpException}
import com.github.fsanaulla.api._
import com.github.fsanaulla.api.management._
import com.github.fsanaulla.model._
import com.github.fsanaulla.utils.TypeAlias._
import com.github.fsanaulla.utils.{AkkaQueryHandler, AkkaRequestHandler, AkkaResponseHandler}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.08.17
  */
private[fsanaulla] class InfluxAkkaHttpClient(host: String,
                                              port: Int = 8086,
                                              username: Option[String] = None,
                                              password: Option[String] = None)
                                             (implicit val ex: ExecutionContext, val system: ActorSystem)

    extends AkkaRequestHandler
      with AkkaResponseHandler
      with AkkaQueryHandler
      with HasCredentials
      with SystemManagement[HttpMethod]
      with DatabaseManagement[HttpResponse, Uri, HttpMethod, RequestEntity]
      with UserManagement[HttpResponse, Uri, HttpMethod, RequestEntity]
      with RetentionPolicyManagement[HttpResponse, Uri, HttpMethod, RequestEntity]
      with ContinuousQueryManagement[HttpResponse, Uri, HttpMethod, RequestEntity]
      with ShardManagement[HttpResponse, Uri, HttpMethod, RequestEntity]
      with SubscriptionManagement[HttpResponse, Uri, HttpMethod, RequestEntity] {

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
  def database(dbName: String): AkkaDatabase = new AkkaDatabase(dbName)

  /**
    *
    * @param dbName - database name
    * @param measurementName - measurement name
    * @tparam A - Measurement's time series type
    * @return - Measurement instance of type [A]
    */
  def measurement[A](dbName: String, measurementName: String): AkkaMeasurement[A] = {
    new AkkaMeasurement[A](dbName, measurementName)
  }

  /**
    * Ping InfluxDB
    */
  def ping(): Future[Result] = {
    buildRequest("/ping", GET).flatMap(toResult)
  }

  /**
    * Close HTTP connection
    */
  def close(): Future[Unit] = Http().shutdownAllConnectionPools()

  /**
    * Close HTTP connection  and  shut down actor system
    */
  def closeAll(): Future[Unit] = for {
    _ <- Http().shutdownAllConnectionPools()
    _ <- system.terminate()
  } yield ()
}

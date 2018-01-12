package com.github.fsanaulla.clients

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethod, HttpResponse, RequestEntity, Uri}
import akka.stream.{ActorMaterializer, StreamTcpException}
import com.github.fsanaulla.api._
import com.github.fsanaulla.api.management._
import com.github.fsanaulla.model.{ConnectionException, InfluxCredentials, UnknownConnectionException}
import com.github.fsanaulla.utils.TypeAlias._
import com.github.fsanaulla.utils.{AkkaQueryHandler, AkkaRequestHandler, AkkaResponseHandler}

import scala.concurrent.ExecutionContext

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
      with SystemApi
      with DatabaseManagement
      with UserManagement
      with RetentionPolicyManagement
      with ContinuousQueryManagement[HttpResponse, Uri, HttpMethod, RequestEntity]
      with ShardManagement
      with SubscriptionManagement {

  private[fsanaulla] implicit val credentials: InfluxCredentials = InfluxCredentials(username, password)
  private[fsanaulla] implicit val mat: ActorMaterializer = ActorMaterializer()
  private[fsanaulla] implicit val connection: Connection = Http().outgoingConnection(host, port) recover {
    case ex: StreamTcpException => throw new ConnectionException(ex.getMessage)
    case unknown => throw new UnknownConnectionException(unknown.getMessage)
  }
}

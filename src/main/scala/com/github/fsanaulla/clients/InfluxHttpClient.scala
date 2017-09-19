package com.github.fsanaulla.clients

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, StreamTcpException}
import com.github.fsanaulla.api._
import com.github.fsanaulla.model.{ConnectionException, InfluxCredentials, UnknownConnectionException}
import com.github.fsanaulla.utils.RequestBuilder
import com.github.fsanaulla.utils.TypeAlias._

import scala.concurrent.ExecutionContext

/**
  * Created by fayaz on 26.06.17.
  */
private[fsanaulla] class InfluxHttpClient(host: String,
                                          port: Int = 8086,
                                          username: Option[String] = None,
                                          password: Option[String] = None)
                                         (implicit val ex: ExecutionContext,
                                          val system: ActorSystem)
  extends SystemApi
    with DatabaseManagement
    with UserManagement
    with RetentionPolicyManagement
    with ContinuousQueryManagement
    with ShardManagement
    with SubscriptionManagement
    with RequestBuilder {

  private[fsanaulla] implicit val credentials: InfluxCredentials = InfluxCredentials(username, password)
  private[fsanaulla] implicit val mat: ActorMaterializer = ActorMaterializer()
  private[fsanaulla] implicit val connection: Connection = Http().outgoingConnection(host, port) recover {
    case ex: StreamTcpException => throw new ConnectionException(ex.getMessage)
    case unknown => throw new UnknownConnectionException(unknown.getMessage)
  }
}

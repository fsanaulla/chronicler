package com.fsanaulla.clients

import akka.actor.{ActorSystem, Terminated}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods.GET
import akka.stream.{ActorMaterializer, StreamTcpException}
import com.fsanaulla.api._
import com.fsanaulla.model.{ConnectionException, InfluxCredentials, Result, UnknownConnectionException}
import com.fsanaulla.utils.RequestBuilder
import com.fsanaulla.utils.ResponseHandler.toResult
import com.fsanaulla.utils.TypeAlias._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by fayaz on 26.06.17.
  */
private[fsanaulla] class InfluxHttpClient(host: String,
                                          port: Int = 8086,
                                          username: Option[String] = None,
                                          password: Option[String] = None)
                                         (implicit val ex: ExecutionContext)
    extends DatabaseManagement
      with UserManagement
      with RetentionPolicyManagement
      with ContinuousQueryManagement
      with ShardManagement
      with SubscriptionManagement
      with RequestBuilder {

  protected implicit val credentials = InfluxCredentials(username, password)
  protected implicit val system = ActorSystem()
  protected implicit val materializer: ActorMaterializer = ActorMaterializer()
  protected implicit val connection: Connection = Http().outgoingConnection(host, port) recover {
    case ex: StreamTcpException => throw new ConnectionException(ex.getMessage)
    case unknown => throw new UnknownConnectionException(unknown.getMessage)
  }

  def use(dbName: String): Database = new Database(dbName)

  def ping(): Future[Result] = buildRequest("/ping", GET).flatMap(toResult)

  def close(): Future[Terminated] = {
    Http().shutdownAllConnectionPools()
    system.terminate()
  }
}

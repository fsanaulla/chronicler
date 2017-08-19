package com.fsanaulla

import akka.actor.{ActorSystem, Terminated}
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, StreamTcpException}
import com.fsanaulla.api._
import com.fsanaulla.model.{ConnectionException, InfluxCredentials, UnknownConnectionException}
import com.fsanaulla.utils.TypeAlias._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by fayaz on 26.06.17.
  */
class InfluxClient(host: String,
                   port: Int = 8086,
                   username: Option[String] = None,
                   password: Option[String] = None)
                  (implicit val ex: ExecutionContext)
    extends DatabaseManagement
      with UserManagement
      with RetentionPolicyManagement
      with ContinuousQueryManagement
      with ShardManagement
      with QuerysManagement
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

  def close(): Future[Terminated] = {
    Http().shutdownAllConnectionPools()
    system.terminate()
  }
}

object InfluxClient {
  def apply(host: String,
            port: Int = 8086,
            username: Option[String] = None,
            password: Option[String] = None)
           (implicit ex: ExecutionContext): InfluxClient = new InfluxClient(host, port, username, password)
}

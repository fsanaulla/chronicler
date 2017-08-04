package com.fsanaulla

import akka.actor.{ActorSystem, Terminated}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.fsanaulla.api.{DataManagement, RequestBuilder, RetentionPolicyManagement, UserManagement}
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
    extends DataManagement
      with UserManagement
      with RetentionPolicyManagement
      with RequestBuilder {

  protected implicit val system = ActorSystem()
  protected implicit val materializer: ActorMaterializer = ActorMaterializer()
  protected implicit val connection: ConnectionPoint = Http().outgoingConnection(host, port)

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

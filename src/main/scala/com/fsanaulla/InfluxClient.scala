package com.fsanaulla

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.fsanaulla.api.{DatabaseManagement, RetentionPolicyManagement, UserManagement}
import com.fsanaulla.model.TypeAlias._

import scala.concurrent.ExecutionContext

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
    with RetentionPolicyManagement {

  private[fsanaulla] implicit val system = ActorSystem()
  private[fsanaulla] implicit val materializer: ActorMaterializer = ActorMaterializer()
  private[fsanaulla] implicit val connection: ConnectionPoint = Http().outgoingConnection(host, port)
}

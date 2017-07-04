package com.fsanaulla

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.fsanaulla.query.InfluxClientQuerys
import com.fsanaulla.utils.TypeAlias._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by fayaz on 26.06.17.
  */
class InfluxClient(host: String,
                   port: Int = 8086,
                   username: String = "influx",
                   password: String = "influx")
                  (implicit ex: ExecutionContext) extends InfluxClientQuerys {

  implicit val system = ActorSystem("system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  private val connection: ConnectionPoint = Http().outgoingConnection(host, port)

  def createDatabase(dbName: String): Future[HttpResponse] = {
    Source.single(
      HttpRequest(
        method = POST,
        uri = createDBQuery(dbName))
    )
      .via(connection)
      .runWith(Sink.head)
  }

  def selectDataBase(dbName: String): Database = new Database(dbName, connection)
}

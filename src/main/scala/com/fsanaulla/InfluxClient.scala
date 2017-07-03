package com.fsanaulla

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.{HttpEntity, HttpRequest, HttpResponse, MediaTypes}
import akka.stream.ActorMaterializer
import akka.util.ByteString
import com.fsanaulla.entity.toPoint
import com.fsanaulla.query.Querys

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by fayaz on 26.06.17.
  */
class InfluxClient(override val host: String,
                   override val port: Int = 8086,
                   username: String = "influx",
                   password: String = "influx")
                  (implicit ex: ExecutionContext) extends Querys {

  implicit val system = ActorSystem("system")
  implicit val materializer = ActorMaterializer()

  def createDatabase(dbName: String): Future[HttpResponse] = {
    Http().singleRequest(
      HttpRequest(
        method = POST,
        uri = createDBQuery(host, port, dbName)))
  }

  def write[T <: toPoint[T]](dbName: String, entity: T): Future[HttpResponse] = {
    Http().singleRequest(
      HttpRequest(
        method = POST,
        uri = writeToDB(dbName),
        entity = HttpEntity(MediaTypes.`application/octet-stream`, ByteString(implicitly[toPoint[T]].write(entity)))
      )
    )
  }

  def bulkWrite[T <: toPoint[T]](dbName: String, entitys: Seq[T]): Future[HttpResponse] = {

    val influxEntitys = entitys.map(implicitly[toPoint[T]].write).mkString("\n")

    Http().singleRequest(
      HttpRequest(
        method = POST,
        uri = writeToDB(dbName),
        entity = HttpEntity(MediaTypes.`application/octet-stream`, ByteString(influxEntitys))
      )
    )
  }
}

package com.fsanaulla

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.{HttpEntity, HttpRequest, MediaTypes}
import akka.stream.ActorMaterializer
import akka.util.ByteString

import scala.concurrent.Future

/**
  * Created by fayaz on 26.06.17.
  */
class InfluxClient(host: String,
                   port: Int = 8086,
                   username: String = "influx",
                   password: String = "influx") {

  implicit val system = ActorSystem("system")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  final val writePath = host + ":" + port + "/write"
  final val queryPath = host + ":" + port + "/query"

  def createDatabase(dbName: String): Future[Boolean] = {
    Http().singleRequest(
      HttpRequest(
        method = POST,
        uri = s"$host:$port/query?q=CREATE DATABASE $dbName"))
      .map(_.status.isSuccess())
  }

  def write(dbName: String): Future[Boolean] = {
    Http().singleRequest(
      HttpRequest(
        method = POST,
        uri = writePath + "?db=" + dbName,
        entity = HttpEntity(MediaTypes.`application/octet-stream`, ByteString("cpu_load_short,host=server02,region=us-west value=0.64"))))
      .map(_.status.isSuccess())
  }

  def bulkWrite(dbName: String) = ???
}

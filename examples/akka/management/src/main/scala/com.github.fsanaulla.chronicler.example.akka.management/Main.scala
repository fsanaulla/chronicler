package com.github.fsanaulla.chronicler.example.akka.management

import akka.actor.ActorSystem
import com.github.fsanaulla.chronicler.akka.management.InfluxMng
import com.github.fsanaulla.chronicler.core.model.Point

import scala.concurrent.Await
import scala.concurrent.duration._

object Main {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem()
    import system.dispatcher

    val host = args.headOption.getOrElse("localhost")
    val influx = InfluxMng(host)

    val result = for {
      // write record to Influx
      createDb <- influx.createDatabase("db") if createDb.isSuccess
      // retrieve written record from Influx
      databases <- influx.showDatabases() if databases.queryResult.nonEmpty
      // close
      _ <- influx.closeAsync
    } yield databases.queryResult

    Await.result(result, Duration.Inf).foreach(println)
  }
}

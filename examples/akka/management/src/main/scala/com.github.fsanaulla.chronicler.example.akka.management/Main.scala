package com.github.fsanaulla.chronicler.example.akka.management

import akka.actor.ActorSystem
import com.github.fsanaulla.chronicler.akka.management.InfluxMng

import scala.util.{Failure, Success}

object Main {

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem()
    import system.dispatcher

    val host   = args.headOption.getOrElse("localhost")
    val influx = InfluxMng(host)

    val result = for {
      // write record to Influx
      _ <- influx.createDatabase("db")
      // retrieve written record from Influx
      databases <- influx.showDatabases()
      // close
      _ <- influx.closeAsync()
    } yield databases

    result.onComplete {
      case Success(Right(dbs)) => dbs.foreach(println)
      case Success(Left(err))  => println(s"Can't retrieve boys coz of: $err")
      case Failure(exception)  => println(s"Execution error: $exception")
    }
  }
}

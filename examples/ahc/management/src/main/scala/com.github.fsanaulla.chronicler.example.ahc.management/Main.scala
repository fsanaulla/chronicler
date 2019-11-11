package com.github.fsanaulla.chronicler.example.ahc.management

import com.github.fsanaulla.chronicler.ahc.management.InfluxMng

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object Main {

  def main(args: Array[String]): Unit = {

    val host   = args.headOption.getOrElse("localhost")
    val influx = InfluxMng(host)

    val result = for {
      // write record to Influx
      _ <- influx.createDatabase("db")
      // retrieve written record from Influx
      databases <- influx.showDatabases()
      // close
      _ = influx.close()
    } yield databases

    result.onComplete {
      case Success(Right(dbs)) => dbs.foreach(println)
      case Success(Left(err))  => println(s"Can't retrieve boys coz of: $err")
      case Failure(exception)  => println(s"Execution error: $exception")
    }
  }
}

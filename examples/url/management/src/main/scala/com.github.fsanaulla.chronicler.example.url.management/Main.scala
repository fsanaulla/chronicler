package com.github.fsanaulla.chronicler.example.url.management

import scala.util.{Failure, Success}
import com.github.fsanaulla.chronicler.sync.management.InfluxMng

object Main {

  def main(args: Array[String]): Unit = {
    val host   = args.headOption.getOrElse("localhost")
    val influx = InfluxMng(host)

    val result = for {
      // write record to Influx
      _ <- influx.createDatabase("db")
      // retrieve written record from Influx
      databases <- influx.showDatabases
      // close
      _ = influx.close()
    } yield databases

    result match {
      case Success(Right(dbs)) => dbs.foreach(println)
      case Success(Left(err))  => println(s"Can't retrieve boys coz of: $err")
      case Failure(exception)  => println(s"Execution error: $exception")
    }
  }
}

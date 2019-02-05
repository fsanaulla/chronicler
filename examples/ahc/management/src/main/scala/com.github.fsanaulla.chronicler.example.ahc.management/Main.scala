package com.github.fsanaulla.chronicler.example.ahc.management

import com.github.fsanaulla.chronicler.ahc.management.InfluxMng
import com.github.fsanaulla.chronicler.core.model.Point

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object Main {
  def main(args: Array[String]): Unit = {

    val host = args.headOption.getOrElse("localhost")
    val influx = InfluxMng(host)

    val result = for {
      // write record to Influx
      createDb <- influx.createDatabase("db") if createDb.isSuccess
      // retrieve written record from Influx
      databases <- influx.showDatabases() if databases.queryResult.nonEmpty
      // close
      _ = influx.close()
    } yield databases.queryResult

    Await.result(result, Duration.Inf).foreach(println)
  }
}

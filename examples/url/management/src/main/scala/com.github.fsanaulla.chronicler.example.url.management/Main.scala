package com.github.fsanaulla.chronicler.example.url.management

import com.github.fsanaulla.chronicler.core.model.Point
import com.github.fsanaulla.chronicler.urlhttp.management.InfluxMng

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

    result.foreach(_.foreach(println))
  }
}

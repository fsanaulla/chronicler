package com.github.fsanaulla.chronicler.example.url.io

import com.github.fsanaulla.chronicler.macros.annotations.{field, tag}
import com.github.fsanaulla.chronicler.macros.auto._
import com.github.fsanaulla.chronicler.urlhttp.io.InfluxIO

import scala.util.{Failure, Success}

object Main {

  def main(args: Array[String]): Unit = {
    final case class Dogs(@tag name: String, @field age: Int)

    // generate formatter at compile-time
    val t      = Dogs("f", 1)
    val host   = args.headOption.getOrElse("localhost")
    val influx = InfluxIO(host)
    val meas   = influx.measurement[Dogs]("db", "cpu")

    val result = for {
      // write record to Influx
      _ <- meas.write(t)
      // retrieve written record from Influx
      dogs <- meas.read("SELECT * FROM cpu")
      // close
      _ = influx.close()
    } yield dogs

    result match {
      case Success(Right(dogs)) => dogs.foreach(b => println(b.name))
      case Success(Left(err))   => println(s"Can't retrieve boys coz of: $err")
      case Failure(exception)   => println(s"Execution error: $exception")
    }
  }
}

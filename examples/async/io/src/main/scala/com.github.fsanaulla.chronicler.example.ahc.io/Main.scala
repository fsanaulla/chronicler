package com.github.fsanaulla.chronicler.example.ahc.io

import com.github.fsanaulla.chronicler.async.io.InfluxIO
import com.github.fsanaulla.chronicler.macros.annotations.{field, tag}
import com.github.fsanaulla.chronicler.macros.auto._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object Main {

  def main(args: Array[String]): Unit = {
    final case class Boy(@tag name: String, @field age: Int)

    val t      = Boy("f", 1)
    val host   = args.headOption.getOrElse("localhost")
    val influx = InfluxIO(host)
    val meas   = influx.measurement[Boy]("db", "cpu")

    val result = for {
      // write record to Influx
      _ <- meas.write(t)
      // retrieve written record from Influx
      boys <- meas.read("SELECT * FROM cpu")
      // close client
      _ = influx.close()
    } yield boys

    result.onComplete {
      case Success(Right(boys)) => boys.foreach(b => println(b.name))
      case Success(Left(err))   => println(s"Can't retrieve boys coz of: $err")
      case Failure(exception)   => println(s"Execution error: $exception")
    }
  }
}

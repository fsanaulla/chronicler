package com.github.fsanaulla.chronicler.example.akka.io

import akka.actor.ActorSystem
import com.github.fsanaulla.chronicler.akka.io.InfluxIO
import com.github.fsanaulla.chronicler.macros.annotations.{field, tag}
import com.github.fsanaulla.chronicler.macros.auto._

import scala.concurrent.Future
import scala.util.{Failure, Success}

object Main {

  def main(args: Array[String]): Unit = {
    final case class Girl(@tag name: String, @field age: Int)

    implicit val system: ActorSystem = ActorSystem()

    import system.dispatcher

    val t      = Girl("f", 1)
    val host   = args.headOption.getOrElse("localhost")
    val influx = InfluxIO(host)
    val meas   = influx.measurement[Girl]("db", "cpu")

    val result = for {
      // write record to Influx
      _ <- meas.write(t)
      // retrieve written record from Influx
      girls <- meas.read("SELECT * FROM cpu")
      // close client
      _ <- Future.successful(influx.close())

    } yield girls

    result.onComplete {
      case Success(Right(girls)) => girls.foreach(b => println(b.name))
      case Success(Left(err))    => println(s"Can't retrieve boys coz of: $err")
      case Failure(exception)    => println(s"Execution error: $exception")
    }
  }
}

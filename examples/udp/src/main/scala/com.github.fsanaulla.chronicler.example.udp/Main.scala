package com.github.fsanaulla.chronicler.example.udp

import com.github.fsanaulla.chronicler.macros.annotations.{field, tag}
import com.github.fsanaulla.chronicler.macros.auto._
import com.github.fsanaulla.chronicler.udp.InfluxUdp

import scala.util.Try

object Main {

  def main(args: Array[String]): Unit = {
    final case class Test(@tag name: String, @field age: Int)
    val t      = Test("f", 1)
    val host   = args.headOption.getOrElse("localhost")
    val influx = InfluxUdp(host)

    val action = for {
      // write record to Influx
      _ <- influx.write("cpu", t)
      // close client
      _ <- Try(influx.close())
    } yield println("Stored!")

    action.get
  }
}

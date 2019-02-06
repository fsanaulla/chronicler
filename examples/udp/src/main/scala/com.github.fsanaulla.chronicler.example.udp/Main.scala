package com.github.fsanaulla.chronicler.example.udp

import com.github.fsanaulla.chronicler.core.model.{InfluxFormatter, Point}
import com.github.fsanaulla.chronicler.macros.Influx
import com.github.fsanaulla.chronicler.macros.annotations.{field, tag}
import com.github.fsanaulla.chronicler.udp.InfluxUdp

object Main {
  def main(args: Array[String]): Unit = {
    final case class Test(@tag name: String, @field age: Int)

    // generate formatter at compile-time
    implicit val fmt: InfluxFormatter[Test] = Influx.formatter[Test]

    val t = Test("f", 1)
    val host = args.headOption.getOrElse("localhost")
    val influx = InfluxUdp(host)

    for {
      // write record to Influx
      _ <- influx.write("cpu", t)
      // retrieve written record from Influx
      // close client
      _ <- influx.close()
    } yield println("Stored!")
  }
}

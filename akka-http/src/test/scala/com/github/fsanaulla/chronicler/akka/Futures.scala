package com.github.fsanaulla.chronicler.akka

import org.scalatest.Suite
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Second, Seconds, Span}

trait FuturesHandler extends ScalaFutures { self: Suite =>
  implicit val pc: PatienceConfig = PatienceConfig(Span(20, Seconds), Span(1, Second))
}

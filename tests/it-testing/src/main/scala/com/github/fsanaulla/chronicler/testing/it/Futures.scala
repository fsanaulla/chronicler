package com.github.fsanaulla.chronicler.testing.it

import org.scalatest.Suite
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Second, Seconds, Span}

trait Futures extends ScalaFutures { self: Suite =>
  implicit val pc: PatienceConfig = PatienceConfig(Span(20, Seconds), Span(1, Second))
}

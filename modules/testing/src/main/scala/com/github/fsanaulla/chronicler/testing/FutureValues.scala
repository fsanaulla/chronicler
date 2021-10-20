package com.github.fsanaulla.chronicler.testing

import org.scalatest.Suite
import org.scalatest.concurrent.{ScalaFutures, IntegrationPatience}
import org.scalatest.time.{Second, Seconds, Span}

trait FutureValues extends ScalaFutures with IntegrationPatience {
  self: Suite =>
}

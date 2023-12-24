package com.github.fsanaulla.chronicler.testing

import org.scalatest.Suite
import org.scalatest.concurrent.IntegrationPatience
import org.scalatest.concurrent.ScalaFutures

trait FutureValues extends ScalaFutures with IntegrationPatience { 
  self: Suite =>
}

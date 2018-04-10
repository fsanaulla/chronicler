package com.github.fsanaulla.core.test

import org.scalatest.Suite
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Second, Seconds, Span}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 10.04.18
  */
trait FutureHandler extends ScalaFutures { self: Suite =>
  implicit val pc: PatienceConfig = PatienceConfig(Span(20, Seconds), Span(1, Second))
}

package com.fsanaulla.utils

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Second, Seconds, Span}
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 11.08.17
  */
trait TestSpec extends FlatSpec with Matchers with ScalaFutures {

  final val host = "localhost"

  implicit val pc = PatienceConfig(Span(20, Seconds), Span(1, Second))
}

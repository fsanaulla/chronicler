package com.github.fsanaulla.core.test.utils

import org.scalatest.OptionValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Second, Seconds, Span}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 11.08.17
  */
trait TestSpec extends FlatSpecWithMatchers with ScalaFutures with OptionValues {

  implicit val pc: PatienceConfig = PatienceConfig(Span(20, Seconds), Span(1, Second))
}

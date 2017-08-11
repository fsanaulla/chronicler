package com.fsanaulla.integration

import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.duration._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 11.08.17
  */
trait IntegrationSpec extends FlatSpec with Matchers {
  implicit val timeout: FiniteDuration = 1 second
  val host = "localhost"
}

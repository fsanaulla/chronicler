package com.fsanaulla

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Created by fayaz on 26.06.17.
  */
object Helper {
  def await[T](f: Future[T])(implicit timeout: Duration): T = Await.result(f, timeout)
}

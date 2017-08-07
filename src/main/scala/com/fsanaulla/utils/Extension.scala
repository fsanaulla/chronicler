package com.fsanaulla.utils

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 07.08.17
  */
object Extension {
  implicit class RichFuture[T](future: Future[T]) {
    def sync(implicit timeout: FiniteDuration): T = Await.result(future, timeout)
  }
}

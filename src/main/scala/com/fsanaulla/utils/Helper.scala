package com.fsanaulla.utils

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 04.08.17
  */
object Helper {

  implicit val timeout: FiniteDuration = 1 second

  def await[T](future: Future[T])(implicit timeout: FiniteDuration): T = Await.result(future, timeout)

}

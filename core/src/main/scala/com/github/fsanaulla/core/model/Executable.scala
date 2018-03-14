package com.github.fsanaulla.core.model

import scala.concurrent.ExecutionContext

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 13.03.18
  */
private [fsanaulla] trait Executable {

  protected implicit val ex: ExecutionContext
}

package com.github.fsanaulla.core.model

import scala.concurrent.ExecutionContext

/***
  * Trait for mixin execution context
  */
private [fsanaulla] trait Executable {

  protected implicit val ex: ExecutionContext
}

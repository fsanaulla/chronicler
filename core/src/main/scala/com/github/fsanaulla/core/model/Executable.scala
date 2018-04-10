package com.github.fsanaulla.core.model

import scala.concurrent.ExecutionContext

/***
  * Trait for mixin execution context
  */
private[fsanaulla] trait Executable {

  /**
    * Implicit execution context
    */
  protected implicit val ex: ExecutionContext
}

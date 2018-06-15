package com.github.fsanaulla.chronicler.core.model

import scala.concurrent.ExecutionContext

/***
  * Trait for mixin execution context
  */
private[chronicler] trait Executable {

  /**
    * Implicit execution context
    */
  protected implicit val ex: ExecutionContext
}

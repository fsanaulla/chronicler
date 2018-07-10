package com.github.fsanaulla.chronicler.core.model

import scala.concurrent.ExecutionContext

/** Trait for mixin execution context */
trait Executable {

  /** Implicit execution context */
  protected implicit val ex: ExecutionContext
}

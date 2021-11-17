package com.github.fsanaulla.chronicler.core.typeclasses

import scala.language.higherKinds

trait MonadError[F[_], Err] extends Monad[F] {
  def fail[A](ex: Err): F[A]
}

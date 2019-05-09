package com.github.fsanaulla.chronicler.core.model

trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B]
}

package com.github.fsanaulla.chronicler.core.typeclasses

import com.github.fsanaulla.chronicler.core.alias.Id

import scala.language.higherKinds

trait Monad[F[_]] {
  def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]
  def pure[A](a: A): F[A]
}

object Monad {
  implicit val monadId: Monad[Id] = new Monad[Id] {
    override def flatMap[A, B](fa: Id[A])(f: A => Id[B]): Id[B] = f(fa)
    override def pure[A](a: A): Id[A]                           = a
  }
}

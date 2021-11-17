package com.github.fsanaulla.chronicler.sync

import com.github.fsanaulla.chronicler.core.alias.Id
import com.github.fsanaulla.chronicler.core.typeclasses.{
  Apply,
  Failable,
  FunctionK,
  Functor,
  Monad,
  MonadError
}
import sttp.client3.{RequestT, Response}

import scala.language.higherKinds
import scala.util.{Failure, Success, Try}

package object shared {
  type RequestE[F[_]] = RequestT[F, Either[String, String], Any]
  type ResponseE      = Response[Either[String, String]]

  implicit val tryFunctor: Functor[Try] = new Functor[Try] {
    override def map[A, B](fa: Try[A])(f: A => B): Try[B] = fa.map(f)
  }

  implicit val tryMonad: Monad[Try] = new Monad[Try] {
    override def flatMap[A, B](fa: Try[A])(f: A => Try[B]): Try[B] =
      fa.flatMap(f)

    override def pure[A](a: A): Try[A] =
      Success(a)
  }

  implicit val tryMonadError: MonadError[Try, Throwable] = new MonadError[Try, Throwable] {
    override def fail[A](ex: Throwable): Try[A]                    = Failure(ex)
    override def flatMap[A, B](fa: Try[A])(f: A => Try[B]): Try[B] = fa.flatMap(f)
    override def pure[A](a: A): Try[A]                             = Success(a)
  }

  implicit val tryApply: Apply[Try] = new Apply[Try] {
    override def pure[A](v: A): Try[A] = Success(v)
  }

  implicit def tryFailable: Failable[Try] = new Failable[Try] {
    override def fail[A](ex: Throwable): Try[A] = Failure(ex)
  }

  implicit val urlFk: FunctionK[Id, Try] = new FunctionK[Id, Try] {
    override def apply[A](fa: Id[A]): Try[A] = Success(fa)
  }
}

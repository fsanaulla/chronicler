package com.github.fsanaulla.chronicler.urlhttp

import com.github.fsanaulla.chronicler.core.alias.Id
import com.github.fsanaulla.chronicler.core.model.{Apply, Failable, FunctionK, Functor}
import sttp.client3.Response

import scala.util.{Failure, Success, Try}

package object shared {
  type ResponseE = Response[Either[String, String]]

  implicit val tryFunctor: Functor[Try] = new Functor[Try] {
    override def map[A, B](fa: Try[A])(f: A => B): Try[B]          = fa.map(f)
    override def flatMap[A, B](fa: Try[A])(f: A => Try[B]): Try[B] = fa.flatMap(f)
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

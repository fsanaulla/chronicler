package com.github.fsanaulla.chronicler.akka

import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.github.fsanaulla.chronicler.core.typeclasses.{Apply, Failable, Functor, MonadError}
import sttp.capabilities.akka.AkkaStreams
import sttp.client3.{RequestT, Response}

import scala.concurrent.{ExecutionContext, Future}

package object shared {
  type RequestE[F[_]] = RequestT[F, Either[String, String], Any]
  type RequestS[F[_]] = RequestT[F, Either[String, Source[ByteString, Any]], Any with AkkaStreams]
  type ResponseE      = Response[Either[String, String]]
  type ResponseS      = Response[Either[String, Source[ByteString, Any]]]

  implicit def futureFunctor(implicit ec: ExecutionContext): Functor[Future] = new Functor[Future] {
    override def map[A, B](fa: Future[A])(f: A => B): Future[B] = fa.map(f)
  }

  implicit def futureMonadError(implicit ec: ExecutionContext): MonadError[Future, Throwable] =
    new MonadError[Future, Throwable] {
      override def fail[A](ex: Throwable): Future[A]                          = Future.failed(ex)
      override def flatMap[A, B](fa: Future[A])(f: A => Future[B]): Future[B] = fa.flatMap(f)
      override def pure[A](a: A): Future[A]                                   = Future.successful(a)
    }

  implicit def futureFailable: Failable[Future] = new Failable[Future] {
    override def fail[A](ex: Throwable): Future[A] = Future.failed(ex)
  }

  implicit val futureApply: Apply[Future] = new Apply[Future] {
    override def pure[A](v: A): Future[A] = Future.successful(v)
  }
}

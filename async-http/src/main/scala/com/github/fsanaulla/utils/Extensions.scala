package com.github.fsanaulla.utils

import com.softwaremill.sttp.{BodySerializer, Request}

import scala.concurrent.Future

private[fsanaulla] object Extensions {
  implicit class RichRequest[T](req: Request[T, Nothing]) {
    final def optBody[B: BodySerializer](body: Option[B]): Request[T, Nothing] = {
      body match {
        case Some(b) => req.body(b)
        case _ => req
      }
    }
  }
  implicit class RichEither[A, B](either: Either[A, B]) {
    final def toFuture(ex: => Throwable): Future[B] = {
      either match {
        case Right(b) => Future.successful(b)
        case Left(_) => Future.failed(ex)
      }
    }
  }
}

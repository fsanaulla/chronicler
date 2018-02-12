package com.github.fsanaulla.utils

import com.softwaremill.sttp.{BodySerializer, Request, Uri}

import scala.concurrent.Future

private[fsanaulla] object Extensions {
  implicit class RichRequest[T](val req: Request[T, Nothing]) extends AnyVal {
    final def optBody[B: BodySerializer](body: Option[B]): Request[T, Nothing] = {
      body match {
        case Some(b) => req.body(b)
        case _ => req
      }
    }
  }
  implicit class RichEither[A, B](val either: Either[A, B]) extends AnyVal {
    final def toFuture(ex: => Throwable): Future[B] = {
      either match {
        case Right(b) => Future.successful(b)
        case Left(_) => Future.failed(ex)
      }
    }
  }

  implicit class RichUri(val uri: Uri) extends AnyVal {
    final def optParams(mp: Map[String, String]): Uri = {
      if (mp.isEmpty) uri
      else uri.params(mp)
    }
  }
}

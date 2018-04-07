package com.github.fsanaulla.async.utils

import scala.util.{Success, Try}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 07.04.18
  */
object Extensions {
  implicit class RichTry[A](private val tr: Try[A]) extends AnyVal {
    def toStrEither(str: String): Either[String, A] = tr match {
      case Success(v) => Right(v)
      case _ => Left(str)
    }
  }
}

package com.github.fsanaulla.chronicler.async.utils

import com.softwaremill.sttp.Response
import jawn.ast.{JParser, JValue}

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

  implicit class RichString(private val str: String) extends AnyVal {
    def toResponse()(implicit p: JParser.type): Response[JValue] = {
      Response(body = p.parseFromString(str).toStrEither(str), 200, "", Nil, Nil)
    }
  }
}

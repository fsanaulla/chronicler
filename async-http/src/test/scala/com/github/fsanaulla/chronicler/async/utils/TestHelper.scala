package com.github.fsanaulla.chronicler.async.utils

import java.net.URLEncoder

import com.github.fsanaulla.chronicler.macros.Macros
import com.github.fsanaulla.chronicler.macros.annotations.{field, tag}

/**
  * Created by fayaz on 11.07.17.
  */
object TestHelper {

  implicit class StringRich(val str: String) extends AnyVal {
    def encode: String = URLEncoder.encode(str)
  }

  final val currentNanoTime: Long = System.currentTimeMillis() * 1000000
  final val AuthErrorResult = WriteResult(401, isSuccess = false, Some(new AuthorizationException("unable to parse authentication credentials")))

  case class FakeEntity(@tag firstName: String,
                        @tag lastName: String,
                        @field age: Int)

  implicit val fmt: InfluxFormatter[FakeEntity] = Macros.format[FakeEntity]

  def queryTesterAuth(query: String)(credentials: InfluxCredentials): String =
    s"http://localhost:8086/query?q=${query.encode}&p=${credentials.password.encode}&u=${credentials.username.encode}"


  def queryTesterAuth(db: String, query: String)(credentials: InfluxCredentials): String =
    s"http://localhost:8086/query?q=${query.encode}&p=${credentials.password.encode}&db=${db.encode}&u=${credentials.username.encode}"


  def queryTester(query: String): String = {
    s"http://localhost:8086/query?q=${query.encode}"
  }

  def queryTester(db: String, query: String): String = {
    s"http://localhost:8086/query?q=${query.encode}&db=${db.encode}"
  }

  def queryTester(path: String, mp: Map[String, String]): String = {
    val s = mp.map {
      case (k, v) => s"$k=${v.encode}"
    }.mkString("&")

    s"http://localhost:8086$path?$s"
  }
}
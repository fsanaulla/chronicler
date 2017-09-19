package com.github.fsanaulla.utils

import akka.http.scaladsl.model.Uri
import com.github.fsanaulla.annotations.{field, formattable, tag}
import com.github.fsanaulla.model._
import spray.json.{DeserializationException, JsArray, JsNumber, JsString}
/**
  * Created by fayaz on 11.07.17.
  */
object TestHelper {

  final val currentNanoTime: Long = System.currentTimeMillis() * 1000000
  final val OkResult = Result(200, isSuccess = true)
  final val NoContentResult = Result(204, isSuccess = true)
  final val AuthErrorResult = Result(401, isSuccess = false, Some(new AuthorizationException("unable to parse authentication credentials")))

  @formattable
  case class FakeEntity(@tag firstName: String,
                        @tag lastName: String,
                        @field age: Int)

  def queryTesterAuth(query: String)(implicit credentials: InfluxCredentials): Uri = Uri("/query").withQuery(Uri.Query("q" -> query, "p" -> credentials.password.get, "u" -> credentials.username.get))

  def queryTesterAuth(db: String, query: String)(implicit credentials: InfluxCredentials): Uri = Uri("/query").withQuery(Uri.Query("q" -> query, "p" -> credentials.password.get, "db" -> db, "u" -> credentials.username.get))

  def queryTester(query: String): Uri = Uri("/query").withQuery(Uri.Query("q" -> query))

  def queryTester(db: String, query: String): Uri = Uri("/query").withQuery(Uri.Query("q" -> query, "db" -> db))

  def writeTester(query: String): Uri = Uri("/write").withQuery(Uri.Query(query))

  def queryTesterSimple(query: Map[String, String]): Uri = Uri("/query").withQuery(Uri.Query(query))
}
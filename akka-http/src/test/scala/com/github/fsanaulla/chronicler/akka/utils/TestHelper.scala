package com.github.fsanaulla.chronicler.akka.utils

import akka.http.scaladsl.model.Uri
import com.github.fsanaulla.core.model._
import spray.json.{DeserializationException, JsArray, JsNumber, JsString}

/**
  * Created by fayaz on 11.07.17.
  */
object TestHelper {

  final val currentNanoTime: Long = System.currentTimeMillis() * 1000000

  case class FakeEntity(firstName: String,
                        lastName: String,
                        age: Int)

  implicit object FormattableFE extends InfluxFormatter[FakeEntity] {
    override def write(obj: FakeEntity): String =
      s"firstName=${obj.firstName},lastName=${obj.lastName} age=${obj.age}"

    override def read(js: JsArray): FakeEntity = js.elements match {
      case Vector(_, JsNumber(age), JsString(fname), JsString(lname)) => FakeEntity(fname, lname, age.toInt)
      case _ => throw DeserializationException(s"Can't deserialize $FakeEntity object")
    }
  }

  def queryTesterAuth(query: String)(credentials: InfluxCredentials): Uri =
    Uri("/query").withQuery(Uri.Query("q" -> query, "p" -> credentials.username, "u" -> credentials.username))

  def queryTesterAuth(db: String, query: String)(credentials: InfluxCredentials): Uri =
    Uri("/query").withQuery(Uri.Query("q" -> query, "p" -> credentials.password, "db" -> db, "u" -> credentials.username))

  def queryTester(query: String): Uri = Uri("/query").withQuery(Uri.Query("q" -> query))

  def queryTester(db: String, query: String): Uri = Uri("/query").withQuery(Uri.Query("q" -> query, "db" -> db))

  def writeTester(mp: Map[String, String]): Uri = Uri("/write").withQuery(Uri.Query(mp))

  def queryTesterSimple(query: Map[String, String]): Uri = Uri("/query").withQuery(Uri.Query(query))
}
package com.fsanaulla.utils

import akka.http.scaladsl.model.Uri
import com.fsanaulla.model.{InfluxReader, InfluxWriter, Result}
import spray.json.{DeserializationException, JsArray, JsNumber, JsString}
/**
  * Created by fayaz on 11.07.17.
  */
object TestHelper {

  final val currentNanoTime: Long = System.currentTimeMillis() * 1000000
  final val OkResult = Result(200, isSuccess = true)
  final val NoContentResult = Result(204, isSuccess = true)

  case class FakeEntity(firstName: String, lastName: String, age: Int)

  implicit object InfluxWriterFakeEntity extends InfluxWriter[FakeEntity] {
    override def write(obj: FakeEntity): String = {
      s"firstName=${obj.firstName},lastName=${obj.lastName} age=${obj.age} $currentNanoTime"
    }
  }

  implicit object InfluxReaderFakeEntity extends InfluxReader[FakeEntity] {
    override def read(js: JsArray): FakeEntity = js.elements match {
      case Vector(_, JsNumber(age), JsString(name), JsString(lastName)) => FakeEntity(name, lastName, age.toInt)
      case _ => throw DeserializationException("Can't deserialize FakeEntity object")
    }
  }

  def queryTester(query: String): Uri = Uri(s"/query?q=$query")

  def writeTester(query: String): Uri = Uri(s"/write?$query")

  def queryTester(db: String, query: String): Uri = Uri(s"/query?db=$db&q=$query")

  def queryTesterSimple(query: Map[String, String]): Uri = Uri("/query").withQuery(Uri.Query(query))
}
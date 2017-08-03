package com.fsanaulla.utils

import akka.http.scaladsl.model.Uri
import com.fsanaulla.model.{InfluxReader, InfluxWriter, Result}
import spray.json.{DeserializationException, JsArray, JsNumber, JsString}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
/**
  * Created by fayaz on 11.07.17.
  */
object Helper extends {

  implicit val timeout: FiniteDuration = 1 second
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

  def await[T](future: Future[T])(implicit timeout: FiniteDuration): T = Await.result(future, timeout)

  def queryTester(query: String): Uri = Uri(s"/query?q=$query")

  def queryTester(db: String, query: String): Uri = Uri(s"/query?db=$db&q=$query")
}
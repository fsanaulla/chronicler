package com.fsanaulla

import com.fsanaulla.model.{InfluxReader, InfluxWriter}
import com.fsanaulla.utils.JsonSupport
import spray.json.{DeserializationException, JsArray, JsNumber, JsString}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
/**
  * Created by fayaz on 11.07.17.
  */
object Helper extends JsonSupport {

  implicit val timeout: FiniteDuration = 1 second
  val currentNanoTime: Long = System.currentTimeMillis() * 1000000

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
}
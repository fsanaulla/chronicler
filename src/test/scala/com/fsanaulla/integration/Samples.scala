package com.fsanaulla.integration

import com.fsanaulla.model.{InfluxReader, InfluxWriter, JsonSupport}
import spray.json.{DeserializationException, JsArray, JsNumber, JsString}
/**
  * Created by fayaz on 11.07.17.
  */
object Samples extends JsonSupport {

  case class FakeEntity(firstName: String, lastName: String, age: Int)

  implicit object InfluxWriterFakeEntity extends InfluxWriter[FakeEntity] {
    override def write(obj: FakeEntity): String = {
      s"firstName=${obj.firstName},lastName=${obj.lastName} age=${obj.age}"
    }
  }

  implicit object InfluxReaderFakeEntity extends InfluxReader[FakeEntity] {
    override def read(js: JsArray): FakeEntity = {
      js.elements match {
        case Vector(_, JsNumber(age), JsString(name), JsString(lastName)) =>
          FakeEntity(name, lastName, age.toInt)
        case _ => throw DeserializationException("Can't deserialize FakeEntity object")
      }
    }
  }

  val singleEntity = FakeEntity("Martin", "Odersky", 58)
}
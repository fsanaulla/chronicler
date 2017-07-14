package com.fsanaulla.integration

import com.fsanaulla.model.{InfluxReader, InfluxWriter, JsonSupport}
import spray.json.JsArray
/**
  * Created by fayaz on 11.07.17.
  */
object Samples extends JsonSupport {

  case class FakeEntity(firstName: String, lastName: String, age: Int)

  implicit object InfluxWriterFakeEntity extends InfluxWriter[FakeEntity] {
    override def write(obj: FakeEntity): String = {
      s"firstName=${obj.firstName},lastName=${obj.lastName} age=${obj.age}}"
    }
  }

  implicit object InfluxReaderFakeEntity extends InfluxReader[FakeEntity] {
    override def read(js: JsArray): FakeEntity = {
      val fields = js.elements
      FakeEntity(
        fields(3).convertTo[String],
        fields(4).convertTo[String],
        fields(1).convertTo[Int]
      )
    }
  }

  val singleEntity = FakeEntity("Martin", "Odersky", 58)
}
package com.github.fsanaulla.chronicler.testing.it

import com.github.fsanaulla.chronicler.core.jawn._
import com.github.fsanaulla.chronicler.core.model.{InfluxReader, InfluxWriter, ParsingException}
import jawn.ast.{JArray, JNum, JString}

case class FakeEntity(sex: String, firstName: String, lastName: String, age: Int)

object FakeEntity {

  def apply(firstName: String,
            lastName: String,
            age: Int): FakeEntity = new FakeEntity(sex = "Male", firstName = firstName, lastName = lastName, age = age)

  implicit val rd: InfluxReader[FakeEntity] = (js: JArray) =>
    js.vs.tail match {
      case Array(age: JNum, firstName: JString, lastName: JString, sex: JString) =>
        Right(FakeEntity(sex, firstName, lastName, age))
      case _ => Left(new ParsingException("Can't deserialize FakeEntity"))
    }

  implicit val wr: InfluxWriter[FakeEntity] = (obj: FakeEntity) => {
    Right(s"sex=${obj.sex},firstName=${obj.firstName},lastName=${obj.lastName} age=${obj.age}")
  }
}

package com.github.fsanaulla.chronicler.testing.it

import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.jawn._
import com.github.fsanaulla.chronicler.core.model.{InfluxReader, InfluxWriter, ParsingException}
import jawn.ast.{JArray, JNum, JString}

case class FakeEntity(sex: String, firstName: String, lastName: String, age: Int)

object FakeEntity {

  def apply(firstName: String,
            lastName: String,
            age: Int): FakeEntity = new FakeEntity(sex = "Male", firstName = firstName, lastName = lastName, age = age)

  // to be compatible with scala 2.11
  implicit val rd: InfluxReader[FakeEntity] = new InfluxReader[FakeEntity] {
    override def read(js: JArray): ErrorOr[FakeEntity] = js.vs.tail match {
      case Array(age: JNum, firstName: JString, lastName: JString, sex: JString) =>
        Right(FakeEntity(sex, firstName, lastName, age))
      case _ =>
        Left(new ParsingException("Can't deserialize FakeEntity"))
    }
  }

  // to be compatible with scala 2.11
  implicit val wr: InfluxWriter[FakeEntity] = new InfluxWriter[FakeEntity] {
    override def write(obj: FakeEntity): ErrorOr[String] =
      Right(s"sex=${obj.sex},firstName=${obj.firstName},lastName=${obj.lastName} age=${obj.age}")
  }
}

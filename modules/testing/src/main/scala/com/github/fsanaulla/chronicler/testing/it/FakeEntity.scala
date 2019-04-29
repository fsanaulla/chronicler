package com.github.fsanaulla.chronicler.testing.it

import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.jawn._
import com.github.fsanaulla.chronicler.core.model.{InfluxFormatter, ParsingException}
import jawn.ast.{JArray, JNum, JString}

case class FakeEntity(sex: String, firstName: String, lastName: String, age: Int)

object FakeEntity {

  def apply(firstName: String,
            lastName: String,
            age: Int): FakeEntity = new FakeEntity(sex = "Male", firstName = firstName, lastName = lastName, age = age)

  implicit val fmt: InfluxFormatter[FakeEntity] = new InfluxFormatter[FakeEntity] {
    override def read(js: JArray): ErrorOr[FakeEntity] = js.vs.tail match {
      case Array(age: JNum, firstName: JString, lastName: JString, sex: JString) =>
        Right(FakeEntity(sex, firstName, lastName, age))
      case _ => Left(new ParsingException("Can't deserialize FakeEntity"))
    }

    override def write(obj: FakeEntity): String =
      s"sex=${obj.sex},firstName=${obj.firstName},lastName=${obj.lastName} age=${obj.age}"
  }
}

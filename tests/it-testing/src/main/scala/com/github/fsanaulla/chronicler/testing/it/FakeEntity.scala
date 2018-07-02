package com.github.fsanaulla.chronicler.testing.it

import com.github.fsanaulla.chronicler.core.model.InfluxFormatter
import com.github.fsanaulla.chronicler.core.utils.PrimitiveJawnImplicits._
import jawn.ast.{JArray, JNum, JString}

case class FakeEntity(sex: String,
                      firstName: String,
                      lastName: String,
                      age: Int)

object FakeEntity {

  def apply(firstName: String,
            lastName: String,
            age: Int): FakeEntity = new FakeEntity(sex = "Male", firstName = firstName, lastName = lastName, age = age)

  implicit val fmt: InfluxFormatter[FakeEntity] = new InfluxFormatter[FakeEntity] {
    override def read(js: JArray): FakeEntity = (js.vs.tail: @unchecked) match {
      case Array(age: JNum, firstName: JString, lastName: JString, sex: JString) =>
        FakeEntity(sex, firstName, lastName, age)
    }

    override def write(obj: FakeEntity): String =
      s"sex=${obj.sex},firstName=${obj.firstName},lastName=${obj.lastName} age=${obj.age}"
  }
}

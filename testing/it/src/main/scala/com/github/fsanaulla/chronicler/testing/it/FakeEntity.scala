package com.github.fsanaulla.chronicler.testing.it

import com.github.fsanaulla.chronicler.core.model.InfluxFormatter
import com.github.fsanaulla.chronicler.macros.Macros
import com.github.fsanaulla.chronicler.macros.annotations.{field, tag}

case class FakeEntity(@tag sex: String = "Male",
                      @tag firstName: String,
                      @tag lastName: String,
                      @field age: Int)

object FakeEntity {

  def apply(firstName: String,
            lastName: String,
            age: Int): FakeEntity = new FakeEntity(firstName = firstName, lastName = lastName, age = age)

  implicit val fmt: InfluxFormatter[FakeEntity] = Macros.format[FakeEntity]
}

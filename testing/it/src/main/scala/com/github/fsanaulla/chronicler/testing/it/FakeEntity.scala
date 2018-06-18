package com.github.fsanaulla.chronicler.testing.it

import com.github.fsanaulla.chronicler.core.model.InfluxFormatter
import com.github.fsanaulla.chronicler.macros.Macros
import com.github.fsanaulla.chronicler.macros.annotations.{field, tag}

case class FakeEntity(@tag firstName: String,
                      @tag lastName: String,
                      @field age: Int)

object FakeEntity {
  implicit val fmt: InfluxFormatter[FakeEntity] = Macros.format[FakeEntity]
}

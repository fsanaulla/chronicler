package com.github.fsanaulla.chronicler.async.utils

import TestHelper._
import jawn.ast.{JArray, JNum, JString}

object SampleEntitys {

  // INTEGRATION SPEC ENTITYS
  val singleEntity = FakeEntity("Martin", "Odersky", 58)

  val singleJsonEntity =
    JArray(Array(
      JNum(currentNanoTime),
      JNum(58),
      JString("Martin"),
      JString("Odersky"))
    )

  val multiEntitys = Array(FakeEntity("Harold", "Lois", 44), FakeEntity("Harry", "Potter", 21))

  val multiJsonEntity = Array(
    JArray(Array(
      JNum(currentNanoTime),
      JNum(58),
      JString("Martin"),
      JString("Odersky"))),
    JArray(Array(
      JNum(currentNanoTime),
      JNum(44),
      JString("Harold"),
      JString("Lois"))),
    JArray(Array(
      JNum(currentNanoTime),
      JNum(21),
      JString("Harry"),
      JString("Potter"))
    )
  )

  val largeMultiJsonEntity = Array(
    Array(
      JArray(Array(
        JNum(currentNanoTime),
        JNum(54),
        JString("Martin"),
        JString("Odersky"))),
      JArray(Array(
        JNum(currentNanoTime),
        JNum(36),
        JString("Jame"),
        JString("Franko"))),
      JArray(Array(
        JNum(currentNanoTime),
        JNum(54),
        JString("Martin"),
        JString("Odersky")))
    ),
    Array(
      JArray(Array(
        JNum(currentNanoTime),
        JNum(36),
        JString("Jame"),
        JString("Franko")))
    )
  )

  // UNIT SPEC ENTITYS
  val singleResult: Array[JArray] = Array(
    JArray(Array(
      JString("2015-01-29T21:55:43.702900257Z"),
      JNum(2))),
    JArray(Array(
      JString("2015-01-29T21:55:43.702900257Z"),
      JNum(0.55))),
    JArray(Array(
      JString("2015-06-11T20:46:02Z"),
      JNum(0.64)))
  )

  val bulkResult = Array(
    Array(
      JArray(Array(
        JString("2015-01-29T21:55:43.702900257Z"),
        JNum(2))),
      JArray(Array(
        JString("2015-01-29T21:55:43.702900257Z"),
        JNum(0.55))),
      JArray(Array(
        JString("2015-06-11T20:46:02Z"),
        JNum(0.64)))
    ),
    Array(
      JArray(Array(
        JString("2015-01-29T21:55:43.702900257Z"),
        JNum(2))),
      JArray(Array(
        JString("2015-01-29T21:55:43.702900257Z"),
        JNum(0.55)))
    )
  )
}

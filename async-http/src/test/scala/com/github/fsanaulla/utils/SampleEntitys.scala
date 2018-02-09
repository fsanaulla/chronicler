package com.github.fsanaulla.utils

import com.github.fsanaulla.utils.TestHelper.{FakeEntity, currentNanoTime}
import spray.json.{JsArray, JsNumber, JsString}

object SampleEntitys {

  // INTEGRATION SPEC ENTITYS
  val singleEntity = FakeEntity("Martin", "Odersky", 58)

  val singleJsonEntity =
    JsArray(
      JsNumber(currentNanoTime),
      JsNumber(58),
      JsString("Martin"),
      JsString("Odersky")
    )

  val multiEntitys = Seq(FakeEntity("Harold", "Lois", 44), FakeEntity("Harry", "Potter", 21))

  val multiJsonEntity = Seq(
    JsArray(
      JsNumber(currentNanoTime),
      JsNumber(58),
      JsString("Martin"),
      JsString("Odersky")),
    JsArray(
      JsNumber(currentNanoTime),
      JsNumber(44),
      JsString("Harold"),
      JsString("Lois")),
    JsArray(
      JsNumber(currentNanoTime),
      JsNumber(21),
      JsString("Harry"),
      JsString("Potter")
    )
  )

  val largeMultiJsonEntity = Seq(
    Seq(
      JsArray(
        JsNumber(currentNanoTime),
        JsNumber(54),
        JsString("Martin"),
        JsString("Odersky")),
      JsArray(
        JsNumber(currentNanoTime),
        JsNumber(36),
        JsString("Jame"),
        JsString("Franko")),
      JsArray(
        JsNumber(currentNanoTime),
        JsNumber(54),
        JsString("Martin"),
        JsString("Odersky"))
    ),
    Seq(
      JsArray(
        JsNumber(currentNanoTime),
        JsNumber(36),
        JsString("Jame"),
        JsString("Franko"))
    )
  )

  // UNIT SPEC ENTITYS
  val singleResult: Seq[JsArray] = Seq(
    JsArray(
      JsString("2015-01-29T21:55:43.702900257Z"),
      JsNumber(2)),
    JsArray(
      JsString("2015-01-29T21:55:43.702900257Z"),
      JsNumber(0.55)),
    JsArray(
      JsString("2015-06-11T20:46:02Z"),
      JsNumber(0.64))
  )

  val bulkResult = Seq(
    Seq(
      JsArray(
        JsString("2015-01-29T21:55:43.702900257Z"),
        JsNumber(2)),
      JsArray(
        JsString("2015-01-29T21:55:43.702900257Z"),
        JsNumber(0.55)),
      JsArray(
        JsString("2015-06-11T20:46:02Z"),
        JsNumber(0.64))
    ),
    Seq(
      JsArray(
        JsString("2015-01-29T21:55:43.702900257Z"),
        JsNumber(2)),
      JsArray(
        JsString("2015-01-29T21:55:43.702900257Z"),
        JsNumber(0.55))
    )
  )
}

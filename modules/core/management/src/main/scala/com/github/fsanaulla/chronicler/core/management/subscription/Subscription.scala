package com.github.fsanaulla.chronicler.core.management.subscription

import com.github.fsanaulla.chronicler.core.enums.{Destination, Destinations}
import org.typelevel.jawn.ast.{JArray, JValue}
import com.github.fsanaulla.chronicler.core.model.InfluxReader
import com.github.fsanaulla.chronicler.core.jawn._
import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.model.ParsingException

final case class Subscription(
    rpName: String,
    subsName: String,
    destType: Destination,
    addresses: Array[String]
)

object Subscription {
  implicit val reader: InfluxReader[Subscription] = new InfluxReader[Subscription] {
    override def read(js: JArray): ErrorOr[Subscription] = js.vs match {
      case Array(rpName: JValue, subsName: JValue, destType: JValue, JArray(elems)) =>
        Destinations
          .withNameOption(destType)
          .fold[ErrorOr[Subscription]](
            Left(new IllegalArgumentException(s"Unsupported destination type: $destType"))
          ) { d =>
            Right(Subscription(rpName, subsName, d, elems.map(_.asString)))
          }
      case _ =>
        Left(new ParsingException(s"Can't deserialize $Subscription object"))
    }

    override def readUnsafe(js: JArray): Subscription = js.vs match {
      case Array(rpName: JValue, subsName: JValue, destType: JValue, JArray(elems)) =>
        Subscription(rpName, subsName, Destinations.withName(destType), elems.map(_.asString))
      case _ =>
        throw new ParsingException(s"Can't deserialize $Subscription object")
    }
  }
}

package com.github.fsanaulla.chronicler.macros

import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.model.{InfluxReader, InfluxWriter}
import com.github.fsanaulla.chronicler.macros.annotations.reader.epoch
import com.github.fsanaulla.chronicler.macros.annotations.{field, tag, timestamp}
import com.github.fsanaulla.chronicler.macros.auto._
import com.github.fsanaulla.scalacheck.Arb
import jawn.ast._
import org.scalacheck.{Arbitrary, Gen}
import sun.security.provider.PolicyParser.ParsingException

trait InfluxFormat {

  case class Test(@tag name: String,
                  @tag surname: Option[String],
                  @field age: Int,
                  @field schooler: Boolean,
                  @field city: String,
                  @epoch @timestamp time: Long)

  val rd: InfluxReader[Test] = InfluxReader[Test]
  val wr: InfluxWriter[Test] = InfluxWriter[Test]

  implicit val gen: Arbitrary[Test] = Arb.dummy[Test]

  val validStr: Gen[String] = for (s <- Gen.alphaStr if s.nonEmpty && s != null) yield s

  implicit val genArr: Arbitrary[JArray] = Arbitrary {
    gen.arbitrary.map { t =>
      JArray(
        Array(
          JNum(t.time),
          JNum(t.age),
          JString(t.city),
          JString(t.name),
          JBool(t.schooler),
          t.surname.fold[JValue](JNull)(JString(_))
        )
      )
    }
  }


  final def influxWrite(t: Test): ErrorOr[String] = {
    if (t.name.isEmpty) Left(new IllegalArgumentException("Can't be empty string"))
    else {
      val sb = StringBuilder.newBuilder

      sb.append(s"name=")
        .append(t.name)

      for (surname <- t.surname) {

        if (surname.isEmpty) return Left(new IllegalArgumentException("Can't be empty string"))
        else {
          sb.append(",")
            .append("surname=")
            .append(surname)
        }
      }

      sb.append(" ")
        .append(s"age=${t.age}i")
        .append(",")
        .append(s"schooler=${t.schooler}")
        .append(",")
        .append("city=")
        .append("\"")
        .append(t.city)
        .append("\"")

      sb.append(" ")
        .append(t.time)

      Right(sb.toString())
    }
  }

  final def influxRead(jArr: JArray): ErrorOr[Test] = jArr.vs match {
    case Array(time, age, city, name, schooler, surname) =>
      Right(
        Test(
          name.asString,
          surname.getString,
          age.asInt,
          schooler.asBoolean,
          city.asString,
          time.asLong
        )
      )
    case _ =>
      Left(new ParsingException("Can't deserialize Test"))
  }
}

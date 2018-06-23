package com.github.fsanaulla.chronicler.macros.properties

import java.time.Instant

import com.github.fsanaulla.chronicler.core.model.InfluxFormatter
import com.github.fsanaulla.chronicler.macros.Macros
import com.github.fsanaulla.chronicler.macros.annotations.{field, tag, timestamp}
import jawn.ast._
import org.scalacheck.{Arbitrary, Gen}

trait InfluxFormat {

  case class Test(@tag name: String,
                  @tag surname: Option[String],
                  @field age: Int,
                  @field schooler: Boolean,
                  @field city: String,
                  @timestamp time: Long)

  val fmt: InfluxFormatter[Test] = Macros.format[Test]

  val validStr: Gen[String] = for (s <- Gen.alphaStr if s.nonEmpty && s != null) yield s

  val arbInstant: Arbitrary[String] = Arbitrary {
      for {
        millis <- Gen.chooseNum(0L, Instant.MAX.getEpochSecond)
        nanos <- Gen.chooseNum(0, Instant.MAX.getNano)
      } yield {
        Instant.ofEpochMilli(millis).plusNanos(nanos).toString
      }
    }

  val gen: Gen[Test] = for {
    name <- Gen.alphaStr
    surname <- Gen.option(Gen.alphaStr)
    age <- Arbitrary.arbInt.arbitrary
    schooler <- Gen.oneOf(true :: false :: Nil)
    city <- Gen.alphaStr
    time <- Arbitrary.arbLong.arbitrary
  } yield Test(name, surname, age, schooler, city, time)

  val genJArr: Gen[JArray] = for {
    name <- validStr
    surname <- Gen.option(validStr)
    age <- Arbitrary.arbInt.arbitrary
    schooler <- Gen.oneOf(true :: false :: Nil)
    city <- validStr
    time <- arbInstant.arbitrary
  } yield
    JArray(
      Array(
        JString(time),
        JNum(age),
        JString(city),
        JString(name),
        JBool(schooler),
        surname.map(s => JString(s)).getOrElse(JNull)
      )
  )

  final def influxWrite(t: Test): String = {
    require(t.name.nonEmpty, "Tag can't be an empty string")
    val sb = StringBuilder.newBuilder

    sb.append(s"name=")
      .append(t.name)

    for (surname <- t.surname) {
      sb.append(",")
        .append("surname=")
        .append(surname)
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
      .toString()
  }

  final def influxRead(jarr: JArray): Test = (jarr.vs: @unchecked) match {
    case Array(time, age, city, name, schooler, surname) =>
      val i = Instant.parse(time.asString)
      Test(
        name.asString,
        surname.getString,
        age.asInt,
        schooler.asBoolean,
        city.asString,
        i.getEpochSecond * 1000000000 + i.getNano
      )
  }
}

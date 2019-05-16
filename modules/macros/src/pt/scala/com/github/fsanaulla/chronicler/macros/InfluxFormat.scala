package com.github.fsanaulla.chronicler.macros

import java.time.Instant

import com.github.fsanaulla.chronicler.core.model.{InfluxReader, InfluxWriter}
import com.github.fsanaulla.chronicler.macros.annotations.reader.epoch
import com.github.fsanaulla.chronicler.macros.annotations.{field, tag, timestamp}
import com.github.fsanaulla.chronicler.macros.auto._
import com.github.fsanaulla.scalacheck.Arb
import jawn.ast._
import org.scalacheck.{Arbitrary, Gen}

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

//  implicit val arbInstant: Arbitrary[Long] = Arbitrary {
//    for {
//      millis <- Gen.chooseNum(0L, Instant.MAX.getEpochSecond)
//    } yield {
//      Instant.ofEpochMilli(millis).plusNanos(nanos).toEpochMilli
//    }
//  }

  implicit val genArr: Arbitrary[JArray] = Arbitrary {
    gen.arbitrary.map { t =>
      JArray(
        Array(
          JNum(t.time),
          JNum(t.age),
          JString(t.city),
          JString(t.name),
          JBool(t.schooler),
          t.surname.map(s => JString(s)).getOrElse(JNull)
        )
      )
    }
  }


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

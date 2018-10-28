package com.gtihub.fsanaulla.chronicler.core.model

import com.github.fsanaulla.chronicler.core.model._
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FlatSpec
import org.scalatest.prop.Checkers

class PointProp extends FlatSpec with Checkers {

  implicit val nonEmptyStringArb: Arbitrary[String] =
    Arbitrary(Gen.nonEmptyListOf[Char](Gen.alphaChar).map(_.mkString))

  implicit val arb: Arbitrary[Point] = Arbitrary {
    for {
      city <- implicitly[Arbitrary[String]].arbitrary
      name <- implicitly[Arbitrary[String]].arbitrary
      age <- implicitly[Arbitrary[Int]].arbitrary
      adult <- implicitly[Arbitrary[Boolean]].arbitrary
      weight <- implicitly[Arbitrary[Double]].arbitrary
      ts <- implicitly[Arbitrary[Long]].arbitrary.filter(_ > 0)
    } yield {
      Point("test")
        .addTag("city", city)
        .addField("name", name)
        .addField("age", age)
        .addField("adult", adult)
        .addField("weight", weight)
        .addTimestamp(ts)
    }
  }

  private val sb = StringBuilder.newBuilder

  "Point" should "be correctly serialized" in check { p: Point =>

    val fields = p.fields
    val city = p.tags.collectFirst { case InfluxTag(key, value) if key == "city" => value }
    val name = fields.collectFirst { case StringField(key, value) if key == "name" => value }
    val age = fields.collectFirst { case IntField(key, value) if key == "age" => value }
    val adult = fields.collectFirst { case BooleanField(key, value) if key == "adult" => value }
    val weight = fields.collectFirst { case DoubleField(key, value) if key == "weight" => value }

    val result = sb
      .append("test,city=").append(city.get).append(" name=").append("\"")
      .append(name.get).append("\",").append("age=").append(age.get)
      .append("i,").append("adult=").append(adult.get).append(",weight=")
      .append(weight.get).append(" ").append(p.time).result()
    sb.clear()

    p.serialize == result
  }
}

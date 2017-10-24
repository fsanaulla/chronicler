package com.github.fsanaulla.unit

import com.github.fsanaulla.macros._
import com.github.fsanaulla.model.{InfluxReader, InfluxWriter}
import org.scalatest.{FlatSpec, Matchers}
import spray.json.{JsArray, JsNumber, JsString}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 08.09.17
  */
class MarcoSpec extends FlatSpec with Matchers {

  private def testWriter[A](o: A)(implicit writer: InfluxWriter[A]): String = {
    writer.write(o)
  }

  private def testReader[A](js: JsArray)(implicit reader: InfluxReader[A]): A = {
    reader.read(js)
  }

  "Writable annotation" should "generate implicit InfluxWriter[A] for single case object" in {

    @writable
    case class TestMacroClass(
                               @tag firstName: String,
                               @tag lastName: String,
                               @tag region: String,
                               friends: List[String] = Nil,
                               @field age: Int) {
      def test = "test"
    }

    val obj = TestMacroClass("A", "B", "C", age = 0)

    testWriter(obj) shouldEqual "firstName=A,lastName=B,region=C age=0"

  }

  it should "generate implicit InfluxWriter[A] for case class with companion object" in {

    @writable
    case class TestMacroClass(@tag firstName: String,
                              @tag lastName: String,
                              @tag region: String,
                              friends: List[String] = Nil,
                              @field age: Int)

    object TestMacroClass {
      val testValue = "Value"
    }

    val obj = TestMacroClass("A", "B", "C", age = 0)

    testWriter(obj) shouldEqual "firstName=A,lastName=B,region=C age=0"
  }

  "Readable annotation" should "generate implicit InfluxReader[A] for single case object" in {
    val jsArray = JsArray(
      JsNumber(3123),
      JsNumber(34),
      JsString("Fayaz")
    )


    @readable
    case class TestMacroClass(name: String, age: Int)

    testReader[TestMacroClass](jsArray) shouldEqual TestMacroClass("Fayaz", 34)
  }

  it should "generate implicit InfluxReader[A] for case class with companion object" in {
    val jsArray = JsArray(
      JsNumber(3123),
      JsNumber(34),
      JsString("Fayaz")
    )

    @readable
    case class TestMacroClass(name: String, age: Int)

    object TestMacroClass

    testReader[TestMacroClass](jsArray) shouldEqual TestMacroClass("Fayaz", 34)
  }

  "Formattable annotation" should "generate implicit InfluxWriter[A]/InfluxReader[A] for single case object" in {

    val jsArray = JsArray(
      JsNumber(3123),
      JsNumber(34),
      JsString("Colin"),
      JsString("James"),
      JsString("us-west")
    )

    @formattable
    case class TestMacroClass(@tag firstName: String,
                              @tag lastName: String,
                              @tag region: String,
                              @field age: Int) {
      def test = "test"
    }

    val obj = TestMacroClass("A", "B", "C", age = 0)

    testWriter(obj) shouldEqual "firstName=A,lastName=B,region=C age=0"
    testReader[TestMacroClass](jsArray) shouldEqual TestMacroClass("Colin", "James", "us-west", age = 34)
  }

  it should "generate implicit InfluxWriter[A]/InfluxReader[A] for case class with companion object" in {

    val jsArray = JsArray(
      JsNumber(3123),
      JsNumber(34),
      JsString("Colin"),
      JsString("James"),
      JsString("us-west")
    )

    @formattable
    case class TestMacroClass(@tag firstName: String,
                              @tag lastName: String,
                              @tag region: String,
                              @field age: Int) {
      def test = "test"
    }

    object TestMacroClass {
      val a = 1
    }

    val obj = TestMacroClass("A", "B", "C", age = 0)

    testWriter(obj) shouldEqual "firstName=A,lastName=B,region=C age=0"
    testReader[TestMacroClass](jsArray) shouldEqual TestMacroClass("Colin", "James", "us-west", age = 34)
  }
}

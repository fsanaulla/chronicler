package com.github.fsanaulla.unit

import com.github.fsanaulla.annotations.{field, format, tag}
import com.github.fsanaulla.model.InfluxWriter
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 08.09.17
  */
class MarcoSpec extends FlatSpec with Matchers {

  "Format annotation" should "generate implicit InfluxWriter[A] with correct write method" in {

    @format
    case class TestMacroClass(
                               @tag firstName: String,
                               @tag lastName: String,
                               @tag region: String,
                                    friends: List[String] = Nil,
                               @field age: Int)

    val obj = TestMacroClass("A", "B", "C", age = 0)

    def test[A](o: A)(implicit writer: InfluxWriter[A]): String = {
      writer.write(o)
    }

    test(obj) shouldEqual "firstName=A,lastName=B,region=C age=0"

  }
}

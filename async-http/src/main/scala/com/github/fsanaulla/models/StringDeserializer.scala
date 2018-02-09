package com.github.fsanaulla.models

import java.io.File

import com.github.fsanaulla.core.model.{Deserializer, Point}

import scala.io.Source

private[fsanaulla] trait StringDeserializer[A] extends Deserializer[A, String] {
  override def deserialize(obj: A): String
}

private[fsanaulla] object StringDeserializer {

  implicit val point2Influx: StringDeserializer[Point] =
    (obj: Point) => obj.serialize

  implicit val points2Influx: StringDeserializer[Seq[Point]] =
    (obj: Seq[Point]) => obj.map(_.serialize).mkString("\n")

  implicit val file2Influx: StringDeserializer[File] =
    (obj: File) => Source.fromFile(obj).getLines().mkString("\n")

  implicit val seqString2Influx: StringDeserializer[Seq[String]] =
    (obj: Seq[String]) => obj.mkString("\n")

  // prevent `any2String` from failing on string
  implicit val str2Influx: StringDeserializer[String] =
    (obj: String) => obj
}

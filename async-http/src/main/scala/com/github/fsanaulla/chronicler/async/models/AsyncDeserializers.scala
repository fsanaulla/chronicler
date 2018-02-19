package com.github.fsanaulla.chronicler.async.models

import java.io.File

import com.github.fsanaulla.core.model.{Deserializer, Point}

import scala.io.Source

private[fsanaulla] object AsyncDeserializers {

  implicit val point2str: Deserializer[Point, String] =
    (obj: Point) => obj.serialize

  implicit val points2str: Deserializer[Seq[Point], String] =
    (obj: Seq[Point]) => obj.map(_.serialize).mkString("\n")

  implicit val file2str: Deserializer[File, String] =
    (obj: File) => Source.fromFile(obj).getLines().mkString("\n")

  implicit val seqString2Influx: Deserializer[Seq[String], String] =
    (obj: Seq[String]) => obj.mkString("\n")

  implicit val str2Influx: Deserializer[String, String] =
    (obj: String) => obj
}

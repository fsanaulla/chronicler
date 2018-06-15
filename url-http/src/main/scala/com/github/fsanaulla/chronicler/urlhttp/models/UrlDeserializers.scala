package com.github.fsanaulla.chronicler.urlhttp.models

import java.io.File

import com.github.fsanaulla.chronicler.core.model.{Deserializer, Point}

import scala.io.Source

private[fsanaulla] object UrlDeserializers {

  implicit val point2str: Deserializer[Point, String] = new Deserializer[Point, String] {
    def deserialize(obj: Point): String = obj.serialize
  }

  implicit val points2str: Deserializer[Seq[Point], String] = new Deserializer[Seq[Point], String] {
    def deserialize(obj: Seq[Point]): String = obj.map(_.serialize).mkString("\n")
  }

  implicit val file2str: Deserializer[File, String] = new Deserializer[File, String] {
    def deserialize(obj: File): String = Source.fromFile(obj).getLines().mkString("\n")
  }

  implicit val seqString2Influx: Deserializer[Seq[String], String] = new Deserializer[Seq[String], String] {
    def deserialize(obj: Seq[String]): String = obj.mkString("\n")
  }

  implicit val str2Influx: Deserializer[String, String] = new Deserializer[String, String] {
    def deserialize(obj: String): String = obj
  }
}

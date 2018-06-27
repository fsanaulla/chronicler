package com.github.fsanaulla.chronicler.urlhttp.models

import com.github.fsanaulla.chronicler.core.model.{Point, Serializer}

private[urlhttp] object UrlDeserializers {

  implicit val point2str: Serializer[Point, String] = new Serializer[Point, String] {
    def serialize(obj: Point): String = obj.serialize
  }

  implicit val points2str: Serializer[Seq[Point], String] = new Serializer[Seq[Point], String] {
    def serialize(obj: Seq[Point]): String = obj.map(_.serialize).mkString("\n")
  }

  implicit val seqString2Influx: Serializer[Seq[String], String] = new Serializer[Seq[String], String] {
    def serialize(obj: Seq[String]): String = obj.mkString("\n")
  }
}

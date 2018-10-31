package com.github.fsanaulla.chronicler.urlhttp.io

import com.github.fsanaulla.chronicler.core.model.{Point, Serializer}

package object serializers {
  private[urlhttp] implicit val point2str: Serializer[Point, String] = new Serializer[Point, String] {
    def serialize(obj: Point): String = obj.serialize
  }

  private[urlhttp] implicit val points2str: Serializer[Seq[Point], String] = new Serializer[Seq[Point], String] {
    def serialize(obj: Seq[Point]): String = obj.map(_.serialize).mkString("\n")
  }

  private[urlhttp] implicit val seqString2Influx: Serializer[Seq[String], String] = new Serializer[Seq[String], String] {
    def serialize(obj: Seq[String]): String = obj.mkString("\n")
  }
}

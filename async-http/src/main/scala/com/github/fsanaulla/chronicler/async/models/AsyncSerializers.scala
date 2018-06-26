package com.github.fsanaulla.chronicler.async.models

import java.io.File

import com.github.fsanaulla.chronicler.core.model.{Point, Serializer}

import scala.io.Source

private[fsanaulla] object AsyncSerializers {

  implicit val point2str: Serializer[Point, String] = new Serializer[Point, String] {
    def serialize(obj: Point): String = obj.serialize
  }

  implicit val points2str: Serializer[Seq[Point], String] = new Serializer[Seq[Point], String] {
    def serialize(obj: Seq[Point]): String = obj.map(_.serialize).mkString("\n")
  }

  implicit val file2str: Serializer[File, String] = new Serializer[File, String] {
    def serialize(obj: File): String = Source.fromFile(obj).getLines().mkString("\n")
  }

  implicit val seqString2Influx: Serializer[Seq[String], String] = new Serializer[Seq[String], String] {
    def serialize(obj: Seq[String]): String = obj.mkString("\n")
  }

  implicit val str2Influx: Serializer[String, String] = new Serializer[String, String] {
    def serialize(obj: String): String = obj
  }
}

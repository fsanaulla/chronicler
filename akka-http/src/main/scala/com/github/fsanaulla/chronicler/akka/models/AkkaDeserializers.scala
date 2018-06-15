package com.github.fsanaulla.chronicler.akka.models

import java.io.File

import _root_.akka.http.scaladsl.model.{HttpEntity, RequestEntity}
import _root_.akka.stream.scaladsl.FileIO
import _root_.akka.util.ByteString
import com.github.fsanaulla.chronicler.akka.utils.AkkaContentTypes.OctetStream
import com.github.fsanaulla.chronicler.core.model.{Deserializer, Point}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 15.03.18
  */
private[akka] object AkkaDeserializers {

  implicit val str2Http: Deserializer[String, RequestEntity] = new Deserializer[String, RequestEntity] {
    def deserialize(obj: String) = HttpEntity(ByteString(obj))
  }

  implicit val seq2Http: Deserializer[Seq[String], RequestEntity] = new Deserializer[Seq[String], RequestEntity] {
    def deserialize(obj: Seq[String]) = HttpEntity(ByteString(obj.mkString("\n")))
  }

  implicit val point2Http: Deserializer[Point, RequestEntity] = new Deserializer[Point, RequestEntity] {
    def deserialize(obj: Point) = HttpEntity(OctetStream, ByteString(obj.serialize))
  }

  implicit val seqPoint2Http: Deserializer[Seq[Point], RequestEntity] = new Deserializer[Seq[Point], RequestEntity] {
    def deserialize(obj: Seq[Point]) = HttpEntity(OctetStream, ByteString(obj.map(_.serialize).mkString("\n")))
  }

  implicit val file2Http: Deserializer[File, RequestEntity] = new Deserializer[File, RequestEntity] {
    def deserialize(obj: File) = HttpEntity(OctetStream, FileIO.fromPath(obj.toPath, chunkSize = 1024))
  }
}

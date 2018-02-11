package com.github.fsanaulla.models

import java.io.File

import akka.http.scaladsl.model.{HttpEntity, RequestEntity}
import akka.stream.scaladsl.FileIO
import akka.util.ByteString
import com.github.fsanaulla.core.model.{Deserializer, Point}
import com.github.fsanaulla.utils.AkkaContentTypes.OctetStream


private[fsanaulla] object AkkaDeserializers {

  implicit val str2Http: Deserializer[String, RequestEntity] = (obj: String) =>
    HttpEntity(ByteString(obj))

  implicit val seq2Http: Deserializer[Seq[String], RequestEntity] = (obj: Seq[String]) =>
    HttpEntity(ByteString(obj.mkString("\n")))

  implicit val point2Http: Deserializer[Point, RequestEntity] = (obj: Point) =>
    HttpEntity(OctetStream, ByteString(obj.serialize))

  implicit val seqPoint2Http: Deserializer[Seq[Point], RequestEntity] = (obj: Seq[Point]) =>
    HttpEntity(OctetStream, ByteString(obj.map(_.serialize).mkString("\n")))

  implicit val file2Http: Deserializer[File, RequestEntity] = (obj: File) =>
    HttpEntity(OctetStream, FileIO.fromPath(obj.toPath, chunkSize = 1024))
}

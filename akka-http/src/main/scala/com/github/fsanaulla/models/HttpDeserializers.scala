package com.github.fsanaulla.models

import java.io.File

import akka.http.scaladsl.model.{HttpEntity, RequestEntity}
import akka.stream.scaladsl.FileIO
import akka.util.ByteString
import com.github.fsanaulla.core.model.Point
import com.github.fsanaulla.utils.AkkaContentTypes.OctetStream

/**
  * This object contains main implicit transformations for preparing request's HTTP body.
  */
object HttpDeserializers {

  implicit def any2Http[A](obj: A)(implicit writer: HttpDeserializer[A]): RequestEntity = writer.write(obj)

  implicit val str2Http: HttpDeserializer[String] = (obj: String) =>
    HttpEntity(ByteString(obj))

  implicit val seq2Http: HttpDeserializer[Seq[String]] = (obj: Seq[String]) =>
    HttpEntity(ByteString(obj.mkString("\n")))

  implicit val point2Http: HttpDeserializer[Point] = (obj: Point) =>
    HttpEntity(OctetStream, ByteString(obj.serialize))

  implicit val seqPoint2Http: HttpDeserializer[Seq[Point]] = (obj: Seq[Point]) =>
    HttpEntity(OctetStream, ByteString(obj.map(_.serialize).mkString("\n")))

  implicit val file2Http: HttpDeserializer[File] = (obj: File) =>
    HttpEntity(OctetStream, FileIO.fromPath(obj.toPath, chunkSize = 1024))
}

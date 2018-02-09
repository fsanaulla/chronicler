package com.github.fsanaulla.models

import java.io.File

import akka.http.scaladsl.model.{HttpEntity, RequestEntity}
import akka.stream.scaladsl.FileIO
import akka.util.ByteString
import com.github.fsanaulla.core.model.{Deserializer, Point}
import com.github.fsanaulla.utils.AkkaContentTypes.OctetStream

import scala.annotation.implicitNotFound

/**
  * Trait that define functionality for serializing into HTTP body entity
  *
  * @tparam A - type parameter
  */
@implicitNotFound(
  "No HttpDeserializer found for type ${A}. Try to implement an implicit HttpWriter for this type."
)
private[fsanaulla] trait HttpDeserializer[A] extends Deserializer[A, RequestEntity] {
  override def deserialize(obj: A): RequestEntity
}

private[fsanaulla] object HttpDeserializer {

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

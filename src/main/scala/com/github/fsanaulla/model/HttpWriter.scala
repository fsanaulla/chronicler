package com.github.fsanaulla.model

import akka.http.scaladsl.model.{HttpEntity, RequestEntity}
import akka.util.ByteString
import com.github.fsanaulla.utils.ContentTypes.OctetStream

import scala.annotation.implicitNotFound

/**
  * Trait that define functionality for serializing into HTTP body entity
  * @tparam A
  */
@implicitNotFound(
  "No HttpWriter found for type ${A}. Try to implement an implicit HttpWriter for this type."
)
trait HttpWriter[A] {
  def write(obj: A): RequestEntity
}

/**
  * This object contains main implicit transformations for preparing request's HTTP body.
  */
object HttpWriters {

  implicit def any2Http[A](obj: A)(implicit writer: HttpWriter[A]): RequestEntity = writer.write(obj)

  implicit val str2Http: HttpWriter[String] = (obj: String) =>
    HttpEntity(ByteString(obj))

  implicit val seq2Http: HttpWriter[Seq[String]] = (obj: Seq[String]) =>
    HttpEntity(ByteString(obj.mkString("\n")))

  implicit val point2Http: HttpWriter[Point] = (obj: Point) =>
    HttpEntity(OctetStream, ByteString(obj.serialize))

  implicit val seqPoint2Http: HttpWriter[Seq[Point]] = (obj: Seq[Point]) =>
    HttpEntity(OctetStream, ByteString(obj.map(_.serialize).mkString("\n")))
}

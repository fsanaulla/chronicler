package com.github.fsanaulla.chronicler.akka.models

import _root_.akka.http.scaladsl.model.{HttpEntity, RequestEntity}
import _root_.akka.util.ByteString
import com.github.fsanaulla.chronicler.akka.utils.AkkaContentTypes.OctetStream
import com.github.fsanaulla.chronicler.core.model.{Point, Serializer}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 15.03.18
  */
private[akka] object AkkaSeserializers {

  implicit val str2Http: Serializer[String, RequestEntity] = new Serializer[String, RequestEntity] {
    def serialize(obj: String) = HttpEntity(ByteString(obj))
  }

  implicit val seq2Http: Serializer[Seq[String], RequestEntity] = new Serializer[Seq[String], RequestEntity] {
    def serialize(obj: Seq[String]) = HttpEntity(ByteString(obj.mkString("\n")))
  }

  implicit val point2Http: Serializer[Point, RequestEntity] = new Serializer[Point, RequestEntity] {
    def serialize(obj: Point) = HttpEntity(OctetStream, ByteString(obj.serialize))
  }

  implicit val seqPoint2Http: Serializer[Seq[Point], RequestEntity] = new Serializer[Seq[Point], RequestEntity] {
    def serialize(obj: Seq[Point]) = HttpEntity(OctetStream, ByteString(obj.map(_.serialize).mkString("\n")))
  }
}

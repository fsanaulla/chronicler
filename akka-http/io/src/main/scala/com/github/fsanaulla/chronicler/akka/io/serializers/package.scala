/*
 * Copyright 2017-2018 Faiaz Sanaulla
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.fsanaulla.chronicler.akka.io

import akka.http.scaladsl.model.{HttpEntity, RequestEntity}
import akka.util.ByteString
import com.github.fsanaulla.chronicler.akka.shared.types.OctetStream
import com.github.fsanaulla.chronicler.core.model.{Point, Serializer}

package object serializers {
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

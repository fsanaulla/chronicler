/*
 * Copyright 2017-2019 Faiaz Sanaulla
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

package com.github.fsanaulla.chronicler.akka.shared.handlers

import java.io.File

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, RequestEntity}
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.components.BodyBuilder
import com.github.fsanaulla.chronicler.core.either
import com.github.fsanaulla.chronicler.core.either.EitherOps
import com.github.fsanaulla.chronicler.core.model.{Appender, InfluxWriter, Point}

class AkkaBodyBuilder extends BodyBuilder[RequestEntity] with Appender {
  override def fromFile(file: File, enc: String): RequestEntity =
    HttpEntity(
      ContentTypes.`text/plain(UTF-8)`,
      Source.fromIterator[ByteString] { () =>
        scala.io.Source.fromFile(file, enc).getLines().map(ByteString(_))
      }
    )

  override def fromString(string: String): RequestEntity =
    HttpEntity(string)

  override def fromStrings(strings: Seq[String]): RequestEntity =
    HttpEntity(strings.mkString("\n"))

  override def fromPoint(point: Point): RequestEntity =
    HttpEntity(point.serialize)

  override def fromPoints(points: Seq[Point]): RequestEntity =
    HttpEntity(points.map(_.serialize).mkString("\n"))

  override def fromT[T](meas: String, t: T)(implicit wr: InfluxWriter[T]): ErrorOr[RequestEntity] =
    wr.write(t).mapRight(append(meas, _))

  override def fromSeqT[T](
      meas: String,
      ts: Seq[T]
    )(implicit wr: InfluxWriter[T]
    ): ErrorOr[RequestEntity] =
    either.seq(ts.map(wr.write)).mapRight(append(meas, _))
}

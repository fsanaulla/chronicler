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

package com.github.fsanaulla.chronicler.udp

import java.io.File
import java.net._
import java.nio.charset.{Charset, StandardCharsets}

import com.github.fsanaulla.chronicler.core.model.{InfluxWriter, Point, PointTransformer}

import scala.io.Source
import scala.util.Try

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.08.17
  */
final class InfluxUDPClient(host: String, port: Int)
  extends PointTransformer
    with AutoCloseable {

  private[this] val socket = new DatagramSocket()
  private[this] def buildAndSend(msg: Array[Byte]): Try[Unit] =
    Try(
      socket.send(
        new DatagramPacket(
          msg,
          msg.length,
          new InetSocketAddress(host, port)
        )
      )
    )

  def writeNative(point: String,
                  charset: Charset = StandardCharsets.UTF_8): Try[Unit] =
    buildAndSend(point.getBytes(charset))

  def bulkWriteNative(points: Seq[String],
                      charset: Charset = StandardCharsets.UTF_8): Try[Unit] =
    buildAndSend(points.mkString("\n").getBytes(charset))

  def write[T](measurement: String,
               entity: T,
               charset: Charset = StandardCharsets.UTF_8)
              (implicit writer: InfluxWriter[T]): Try[Unit] = {
    val sendEntity = toPoint(measurement, writer.write(entity))

    buildAndSend(sendEntity.getBytes(charset))
  }

  def bulkWrite[T](measurement: String,
                   entities: Seq[T],
                   charset: Charset = StandardCharsets.UTF_8)
                  (implicit writer: InfluxWriter[T]): Try[Unit] = {
    val sendEntity =
      toPoints(measurement, entities.map(writer.write)).getBytes(charset)

    buildAndSend(sendEntity)
  }

  def writeFromFile(file: File,
                    charset: Charset = StandardCharsets.UTF_8): Try[Unit] = {
    val sendData = Source
      .fromFile(file)
      .getLines()
      .mkString("\n")
      .getBytes(charset)

    buildAndSend(sendData)
  }

  def writePoint(point: Point,
                 charset: Charset = StandardCharsets.UTF_8): Try[Unit] =
    buildAndSend(point.serialize.getBytes(charset))

  def bulkWritePoints(points: Seq[Point],
                      charset: Charset = StandardCharsets.UTF_8): Try[Unit] =
    buildAndSend(
      points
        .map(_.serialize)
        .mkString("\n")
        .getBytes(charset)
    )

  def close(): Unit = socket.close()
}

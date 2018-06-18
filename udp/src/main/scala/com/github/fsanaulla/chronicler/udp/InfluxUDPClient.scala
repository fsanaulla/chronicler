package com.github.fsanaulla.chronicler.udp

import java.io.File
import java.net._

import com.github.fsanaulla.chronicler.core.model.{InfluxWriter, Point}
import com.github.fsanaulla.chronicler.core.utils.PointTransformer

import scala.io.Source

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.08.17
  */
final class InfluxUDPClient(host: String, port: Int)
  extends PointTransformer
    with AutoCloseable {

  import InfluxUDPClient._

  private implicit val conn: UdpConnection = UdpConnection(InetAddress.getByName(host), port)

  def writeNative(point: String): Unit =
    send(buildDatagram(point.getBytes()))


  def bulkWriteNative(points: Seq[String]): Unit =
    send(buildDatagram(points.mkString("\n").getBytes()))


  def write[T](measurement: String, entity: T)(implicit writer: InfluxWriter[T]): Unit = {
    val sendEntity = toPoint(measurement, writer.write(entity)).getBytes()

    send(buildDatagram(sendEntity))
  }

  def bulkWrite[T](measurement: String, entitys: Seq[T])(implicit writer: InfluxWriter[T]): Unit = {
    val sendEntity = toPoints(measurement, entitys.map(writer.write)).getBytes()

    send(buildDatagram(sendEntity))
  }

  def writeFromFile(file: File): Unit = {
    val sendData = Source.fromFile(file).getLines().mkString("\n").getBytes()

    send(buildDatagram(sendData))
  }

  def writePoint(point: Point): Unit =
    send(buildDatagram(point.serialize.getBytes()))

  def bulkWritePoints(points: Seq[Point]): Unit =
    send(buildDatagram(points.map(_.serialize).mkString("\n").getBytes()))

  def close(): Unit = socket.close()
}

private[udp] object InfluxUDPClient {
  private val socket = new DatagramSocket()

  def buildDatagram(msg: Array[Byte])(implicit conn: UdpConnection): DatagramPacket =
    new DatagramPacket(msg, msg.length, conn.address, conn.port)

  def send(dp: DatagramPacket): Unit = socket.send(dp)
}

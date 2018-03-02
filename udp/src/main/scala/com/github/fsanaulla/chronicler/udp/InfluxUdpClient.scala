package com.github.fsanaulla.chronicler.udp

import java.net._

import com.github.fsanaulla.chronicler.udp.models.UdpConnection
import com.github.fsanaulla.core.model.{InfluxWriter, Point}
import com.github.fsanaulla.core.utils.PointTransformer

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.08.17
  */
private[fsanaulla] class InfluxUdpClient(host: String,
                                         port: Int = 8089)
                                        (implicit ex: ExecutionContext)
  extends PointTransformer with AutoCloseable {

  private val socket = new DatagramSocket()

  private implicit val conn: UdpConnection = UdpConnection(InetAddress.getByName(host), port)

  def writeNative(point: String): Future[Unit] = {
    send(buildDatagram(point.getBytes()))
  }

  def bulkWriteNative(points: Seq[String]): Future[Unit] = {
    send(buildDatagram(points.mkString("\n").getBytes()))
  }

  def write[T](measurement: String, entity: T)(implicit writer: InfluxWriter[T]): Future[Unit] = {
    val sendEntity = toPoint(measurement, writer.write(entity)).getBytes()

    send(buildDatagram(sendEntity))
  }

  def bulkWrite[T](measurement: String, entitys: Seq[T])(implicit writer: InfluxWriter[T]): Future[Unit] = {
    val sendEntity = toPoints(measurement, entitys.map(writer.write)).getBytes()

    send(buildDatagram(sendEntity))
  }

  def writeFromFile(filePath: String): Future[Unit] = {
    val sendData = Source.fromFile(filePath).getLines().mkString("\n").getBytes()

    send(buildDatagram(sendData))
  }

  def writePoint(point: Point): Future[Unit] =
    send(buildDatagram(point.serialize.getBytes()))

  def bulkWritePoints(points: Seq[Point]): Future[Unit] =
    send(buildDatagram(points.map(_.serialize).mkString("\n").getBytes()))

  def close(): Unit = socket.close()

  private def buildDatagram(msg: Array[Byte])(implicit conn: UdpConnection): DatagramPacket =
    new DatagramPacket(msg, msg.length, conn.address, conn.port)

  private def send(dp: DatagramPacket): Future[Unit] = Future { socket.send(dp) }
}

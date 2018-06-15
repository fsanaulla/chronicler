package com.github.fsanaulla.chronicler.akka.api

import java.io.File

import _root_.akka.actor.ActorSystem
import _root_.akka.http.scaladsl.model.RequestEntity
import _root_.akka.stream.ActorMaterializer
import com.github.fsanaulla.chronicler.akka.io.{AkkaReader, AkkaWriter}
import com.github.fsanaulla.chronicler.akka.models.AkkaDeserializers._
import com.github.fsanaulla.chronicler.akka.utils.AkkaAlias.Connection
import com.github.fsanaulla.chronicler.core.api.DatabaseApi
import com.github.fsanaulla.chronicler.core.enums._
import com.github.fsanaulla.chronicler.core.model._
import jawn.ast.JArray

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.08.17
  */
final class Database(dbName: String, val credentials: Option[InfluxCredentials])
              (protected implicit val actorSystem: ActorSystem,
               override protected implicit val mat: ActorMaterializer,
               override protected implicit val ex: ExecutionContext,
               override protected implicit val connection: Connection)
  extends DatabaseApi[Future, RequestEntity](dbName)
    with AkkaWriter
    with AkkaReader
    with HasCredentials
    with Executable {

  def writeFromFile(file: File,
                    chunkSize: Int = 8192,
                    consistency: Consistency = Consistencies.ONE,
                    precision: Precision = Precisions.NANOSECONDS,
                    retentionPolicy: Option[String] = None): Future[WriteResult] =
    writeFromFile0(file, chunkSize, consistency, precision, retentionPolicy)

  def writeNative(point: String,
                  consistency: Consistency = Consistencies.ONE,
                  precision: Precision = Precisions.NANOSECONDS,
                  retentionPolicy: Option[String] = None): Future[WriteResult] =
    writeNative0(point, consistency, precision, retentionPolicy)

  def bulkWriteNative(points: Seq[String],
                      consistency: Consistency = Consistencies.ONE,
                      precision: Precision = Precisions.NANOSECONDS,
                      retentionPolicy: Option[String] = None): Future[WriteResult] =
    bulkWriteNative0(points, consistency, precision, retentionPolicy)

  def writePoint(point: Point,
                 consistency: Consistency = Consistencies.ONE,
                 precision: Precision = Precisions.NANOSECONDS,
                 retentionPolicy: Option[String] = None): Future[WriteResult] =
    writePoint0(point, consistency, precision, retentionPolicy)

  def bulkWritePoints(points: Seq[Point],
                      consistency: Consistency = Consistencies.ONE,
                      precision: Precision = Precisions.NANOSECONDS,
                      retentionPolicy: Option[String] = None): Future[WriteResult] =
    bulkWritePoints0(points, consistency, precision, retentionPolicy)

  def read[A: ClassTag](query: String,
                        epoch: Epoch = Epochs.NANOSECONDS,
                        pretty: Boolean = false,
                        chunked: Boolean = false)
                       (implicit reader: InfluxReader[A]): Future[ReadResult[A]] =
    readJs(query, epoch, pretty, chunked) map {
      case qr: QueryResult[JArray] => qr.map(reader.read)
      case gr: GroupedResult[JArray] => gr.map(reader.read)
    }
}

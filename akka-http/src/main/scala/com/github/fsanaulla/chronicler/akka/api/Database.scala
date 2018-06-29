package com.github.fsanaulla.chronicler.akka.api

import _root_.akka.actor.ActorSystem
import _root_.akka.http.scaladsl.model.RequestEntity
import _root_.akka.stream.ActorMaterializer
import com.github.fsanaulla.chronicler.akka.io.{AkkaReader, AkkaWriter}
import com.github.fsanaulla.chronicler.akka.models.AkkaSeserializers._
import com.github.fsanaulla.chronicler.akka.utils.AkkaAlias.Connection
import com.github.fsanaulla.chronicler.core.api.DatabaseIO
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
final class Database(dbName: String,
                     val credentials: Option[InfluxCredentials],
                     gzipped: Boolean = false)
                    (protected implicit val actorSystem: ActorSystem,
                     override protected implicit val mat: ActorMaterializer,
                     override protected implicit val ex: ExecutionContext,
                     override protected implicit val connection: Connection)
  extends DatabaseIO[Future, RequestEntity](dbName)
    with AkkaWriter
    with AkkaReader
    with Serializable[RequestEntity]
    with HasCredentials
    with Executable {

  def writeFromFile(filePath: String,
                    consistency: Consistency = Consistencies.ONE,
                    precision: Precision = Precisions.NANOSECONDS,
                    retentionPolicy: Option[String] = None): Future[WriteResult] =
    writeFromFile(dbName, filePath, consistency, precision, retentionPolicy, gzipped)

  def writeNative(point: String,
                  consistency: Consistency = Consistencies.ONE,
                  precision: Precision = Precisions.NANOSECONDS,
                  retentionPolicy: Option[String] = None): Future[WriteResult] =
    writeTo(dbName, point, consistency, precision, retentionPolicy, gzipped)

  def bulkWriteNative(points: Seq[String],
                      consistency: Consistency = Consistencies.ONE,
                      precision: Precision = Precisions.NANOSECONDS,
                      retentionPolicy: Option[String] = None): Future[WriteResult] =
    writeTo(dbName, points, consistency, precision, retentionPolicy, gzipped)

  def writePoint(point: Point,
                 consistency: Consistency = Consistencies.ONE,
                 precision: Precision = Precisions.NANOSECONDS,
                 retentionPolicy: Option[String] = None): Future[WriteResult] =
    writeTo(dbName, point, consistency, precision, retentionPolicy, gzipped)

  def bulkWritePoints(points: Seq[Point],
                      consistency: Consistency = Consistencies.ONE,
                      precision: Precision = Precisions.NANOSECONDS,
                      retentionPolicy: Option[String] = None): Future[WriteResult] =
    writeTo(dbName, points, consistency, precision, retentionPolicy, gzipped)

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

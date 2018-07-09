package com.github.fsanaulla.chronicler.akka.api

import _root_.akka.actor.ActorSystem
import _root_.akka.http.scaladsl.model.RequestEntity
import _root_.akka.stream.ActorMaterializer
import com.github.fsanaulla.chronicler.akka.io.{AkkaReader, AkkaWriter}
import com.github.fsanaulla.chronicler.akka.utils.AkkaAlias.Connection
import com.github.fsanaulla.chronicler.core.api.MeasurementIO
import com.github.fsanaulla.chronicler.core.enums._
import com.github.fsanaulla.chronicler.core.model._
import jawn.ast.JArray

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 03.09.17
  */
final class Measurement[E: ClassTag](dbName: String,
                                     measurementName: String,
                                     val credentials: Option[InfluxCredentials],
                                     gzipped: Boolean)
                                    (protected implicit val actorSystem: ActorSystem,
                                     protected implicit val mat: ActorMaterializer,
                                     protected implicit val ex: ExecutionContext,
                                     protected implicit val connection: Connection)
    extends MeasurementIO[Future, E, RequestEntity]
      with AkkaWriter
      with AkkaReader
      with HasCredentials
      with Executable
      with PointTransformer {

  def write(entity: E,
            consistency: Consistency = Consistencies.ONE,
            precision: Precision = Precisions.NANOSECONDS,
            retentionPolicy: Option[String] = None)(implicit wr: InfluxWriter[E]): Future[WriteResult] =
    writeTo(
      dbName,
      toPoint(measurementName, wr.write(entity)),
      consistency,
      precision,
      retentionPolicy,
      gzipped
    )


  def bulkWrite(entitys: Seq[E],
                consistency: Consistency = Consistencies.ONE,
                precision: Precision = Precisions.NANOSECONDS,
                retentionPolicy: Option[String] = None)(implicit wr: InfluxWriter[E]): Future[WriteResult] =
    writeTo(
      dbName,
      toPoints(measurementName, entitys.map(wr.write)),
      consistency,
      precision,
      retentionPolicy,
      gzipped
    )


  def read(query: String,
           epoch: Epoch = Epochs.NANOSECONDS,
           pretty: Boolean = false,
           chunked: Boolean = false)(implicit reader: InfluxReader[E]): Future[ReadResult[E]] =
    readJs(dbName, query, epoch, pretty, chunked) map {
      case qr: QueryResult[JArray] => qr.map(reader.read)
      case gr: GroupedResult[JArray] => gr.map(reader.read)
    }

}

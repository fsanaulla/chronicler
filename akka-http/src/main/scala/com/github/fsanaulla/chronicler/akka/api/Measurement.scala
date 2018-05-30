package com.github.fsanaulla.chronicler.akka.api

import _root_.akka.actor.ActorSystem
import _root_.akka.http.scaladsl.model.RequestEntity
import _root_.akka.stream.ActorMaterializer
import com.github.fsanaulla.chronicler.akka.io.{AkkaReader, AkkaWriter}
import com.github.fsanaulla.chronicler.akka.utils.AkkaTypeAlias.Connection
import com.github.fsanaulla.core.api.MeasurementApi
import com.github.fsanaulla.core.enums._
import com.github.fsanaulla.core.model._

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 03.09.17
  */
class Measurement[E: ClassTag](
                                      dbName: String,
                                      measurementName: String,
                                      val credentials: Option[InfluxCredentials])
                                    (protected implicit val actorSystem: ActorSystem,
                                     protected implicit val mat: ActorMaterializer,
                                     protected implicit val ex: ExecutionContext,
                                     protected implicit val connection: Connection)
    extends MeasurementApi[Future, E, RequestEntity](dbName, measurementName)
      with AkkaWriter
      with AkkaReader
      with HasCredentials
      with Executable {

  import com.github.fsanaulla.chronicler.akka.models.AkkaDeserializers.str2Http

  def write(
             entity: E,
             consistency: Consistency = Consistencies.ONE,
             precision: Precision = Precisions.NANOSECONDS,
             retentionPolicy: Option[String] = None)
           (implicit writer: InfluxWriter[E]): Future[Result] =
    write0(entity, consistency, precision, retentionPolicy)


  def bulkWrite(
                 entitys: Seq[E],
                 consistency: Consistency = Consistencies.ONE,
                 precision: Precision = Precisions.NANOSECONDS,
                 retentionPolicy: Option[String] = None)
               (implicit writer: InfluxWriter[E]): Future[Result] =
    bulkWrite0(entitys, consistency, precision, retentionPolicy)


  def read(query: String,
           epoch: Epoch = Epochs.NANOSECONDS,
           pretty: Boolean = false,
           chunked: Boolean = false)
          (implicit reader: InfluxReader[E]): Future[QueryResult[E]] =
    readJs0(dbName, query, epoch, pretty, chunked).map(_.map(reader.read))

}

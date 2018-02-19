package com.github.fsanaulla.chronicler.akka.api

import _root_.akka.actor.ActorSystem
import _root_.akka.http.scaladsl.model.RequestEntity
import _root_.akka.stream.ActorMaterializer
import com.github.fsanaulla.chronicler.akka.utils.AkkaTypeAlias.Connection
import com.github.fsanaulla.chronicler.async.io.AkkaWriter
import com.github.fsanaulla.core.api.MeasurementApi
import com.github.fsanaulla.core.model.{InfluxCredentials, InfluxWriter, Result}
import com.github.fsanaulla.core.utils.constants.Consistencys.Consistency
import com.github.fsanaulla.core.utils.constants.Precisions.Precision
import com.github.fsanaulla.core.utils.constants.{Consistencys, Precisions}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 03.09.17
  */
private[fsanaulla] class Measurement[E](dbName: String, measurementName: String)
                                       (protected implicit val credentials: InfluxCredentials,
                                        protected implicit val actorSystem: ActorSystem,
                                        protected implicit val mat: ActorMaterializer,
                                        protected implicit val ex: ExecutionContext,
                                        protected implicit val connection: Connection)
    extends MeasurementApi[E, RequestEntity](dbName, measurementName) with AkkaWriter {

  import com.github.fsanaulla.chronicler.akka.models.AkkaDeserializers.str2Http

  def write(entity: E,
            consistency: Consistency = Consistencys.ONE,
            precision: Precision = Precisions.NANOSECONDS,
            retentionPolicy: Option[String] = None)
           (implicit writer: InfluxWriter[E]): Future[Result] = {
    write0(entity, consistency, precision, retentionPolicy)
  }

  def bulkWrite(entitys: Seq[E],
                consistency: Consistency = Consistencys.ONE,
                precision: Precision = Precisions.NANOSECONDS,
                retentionPolicy: Option[String] = None)
               (implicit writer: InfluxWriter[E]): Future[Result] = {
    bulkWrite0(entitys, consistency, precision, retentionPolicy)
  }

}

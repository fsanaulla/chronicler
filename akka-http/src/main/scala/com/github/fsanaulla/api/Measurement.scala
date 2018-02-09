package com.github.fsanaulla.api

import akka.actor.ActorSystem
import akka.http.scaladsl.model.RequestEntity
import akka.stream.ActorMaterializer
import com.github.fsanaulla.core.api.MeasurementApi
import com.github.fsanaulla.core.model.{InfluxCredentials, InfluxWriter, Result}
import com.github.fsanaulla.core.utils.constants.Consistencys.Consistency
import com.github.fsanaulla.core.utils.constants.Precisions.Precision
import com.github.fsanaulla.core.utils.constants.{Consistencys, Precisions}
import com.github.fsanaulla.io.AkkaWriter
import com.github.fsanaulla.utils.AkkaTypeAlias.Connection

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

  import com.github.fsanaulla.models.HttpDeserializer.str2Http

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

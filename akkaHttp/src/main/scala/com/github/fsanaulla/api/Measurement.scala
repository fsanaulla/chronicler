package com.github.fsanaulla.api

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpEntity, RequestEntity}
import akka.stream.ActorMaterializer
import akka.util.ByteString
import com.github.fsanaulla.io.AkkaWriter
import com.github.fsanaulla.model.{InfluxCredentials, InfluxWriter, Result}
import com.github.fsanaulla.utils.AkkaContentTypes.OctetStream
import com.github.fsanaulla.utils.AkkaTypeAlias.Connection
import com.github.fsanaulla.utils.constants.Consistencys.Consistency
import com.github.fsanaulla.utils.constants.Precisions.Precision
import com.github.fsanaulla.utils.constants.{Consistencys, Precisions}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 03.09.17
  */
//todo: implement typesafe read operation
private[fsanaulla] class Measurement[E](dbName: String, measurementName: String)
                                       (protected implicit val credentials: InfluxCredentials,
                                            protected implicit val actorSystem: ActorSystem,
                                            protected implicit val mat: ActorMaterializer,
                                            protected implicit val ex: ExecutionContext,
                                            protected implicit val connection: Connection)
    extends MeasurementApi[RequestEntity] with AkkaWriter {

  def write(entity: E,
            consistency: Consistency = Consistencys.ONE,
            precision: Precision = Precisions.NANOSECONDS,
            retentionPolicy: Option[String] = None)(implicit writer: InfluxWriter[E]): Future[Result] = {

    // make http entity serializer
    val serializedEntity = HttpEntity(OctetStream, ByteString(toPoint(measurementName, writer.write(entity))))

    write0(
      dbName,
      serializedEntity,
      consistency,
      precision,
      retentionPolicy
    )
  }

  def bulkWrite(entitys: Seq[E],
                consistency: Consistency = Consistencys.ONE,
                precision: Precision = Precisions.NANOSECONDS,
                retentionPolicy: Option[String] = None)(implicit writer: InfluxWriter[E]): Future[Result] = {

    val entity = HttpEntity(OctetStream, ByteString(toPoints(measurementName, entitys.map(writer.write))))

    write0(
      dbName,
      entity,
      consistency,
      precision,
      retentionPolicy
    )
  }
}

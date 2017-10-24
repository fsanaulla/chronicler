package com.github.fsanaulla.api

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpEntity
import akka.stream.ActorMaterializer
import akka.util.ByteString
import com.github.fsanaulla.model.{InfluxCredentials, InfluxWriter, Result}
import com.github.fsanaulla.utils.ContentTypes.OctetStream
import com.github.fsanaulla.utils.TypeAlias.Connection
import com.github.fsanaulla.utils.WriteHelpersOperation
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
private[fsanaulla] class Measurement[A](dbName: String, measurementName: String)
                                       (private implicit val credentials: InfluxCredentials,
                                      private implicit val actorSystem: ActorSystem,
                                      private implicit val mat: ActorMaterializer,
                                      private implicit val ex: ExecutionContext,
                                      private implicit val connection: Connection)
    extends WriteHelpersOperation {

  def write(entity: A,
            consistency: Consistency = Consistencys.ONE,
            precision: Precision = Precisions.NANOSECONDS,
            retentionPolicy: Option[String] = None)(implicit writer: InfluxWriter[A]): Future[Result] = {

    val serializedEntity = HttpEntity(OctetStream, ByteString(toPoint(measurementName, writer.write(entity))))

    write(
      dbName,
      serializedEntity,
      consistency,
      precision,
      retentionPolicy
    )
  }

  def bulkWrite(entitys: Seq[A],
                consistency: Consistency = Consistencys.ONE,
                precision: Precision = Precisions.NANOSECONDS,
                retentionPolicy: Option[String] = None)(implicit writer: InfluxWriter[A]): Future[Result] = {

    val entity = HttpEntity(OctetStream, ByteString(toPoints(measurementName, entitys.map(writer.write))))

    write(
      dbName,
      entity,
      consistency,
      precision,
      retentionPolicy
    )
  }
}

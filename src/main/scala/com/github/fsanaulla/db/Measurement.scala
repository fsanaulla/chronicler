package com.github.fsanaulla.db

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpEntity
import akka.stream.ActorMaterializer
import akka.util.ByteString
import com.github.fsanaulla.model.{InfluxCredentials, InfluxWriter, Result}
import com.github.fsanaulla.utils.ContentTypes.octetStream
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
class Measurement[A](dbName: String, measurementName: String)
                    (protected implicit val credentials: InfluxCredentials,
                     protected implicit val actorSystem: ActorSystem,
                     protected implicit val mat: ActorMaterializer,
                     protected implicit val ex: ExecutionContext,
                     protected implicit val connection: Connection) extends WriteHelpersOperation{

  def write(entity: A,
            consistency: Consistency = Consistencys.ONE,
            precision: Precision = Precisions.NANOSECONDS,
            retentionPolicy: Option[String] = None)(implicit writer: InfluxWriter[A]): Future[Result] = {
    write(
      dbName,
      HttpEntity(octetStream, ByteString(toPoint(measurementName, writer.write(entity)))),
      consistency,
      precision,
      retentionPolicy
    )
  }

  def bulkWrite(entitys: Seq[A],
                   consistency: Consistency = Consistencys.ONE,
                   precision: Precision = Precisions.NANOSECONDS,
                   retentionPolicy: Option[String] = None)(implicit writer: InfluxWriter[A]): Future[Result] = {
    write(
      dbName,
      HttpEntity(octetStream, ByteString(toPoints(measurementName, entitys.map(writer.write)))),
      consistency,
      precision,
      retentionPolicy
    )
  }
}

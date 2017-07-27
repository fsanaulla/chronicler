package com.fsanaulla.utils

import akka.http.scaladsl.model.HttpResponse
import akka.stream.ActorMaterializer
import com.fsanaulla.model.DatabaseInfo.DatabaseInfoInfluxReader
import com.fsanaulla.model.{DatabaseInfo, InfluxReader, MeasurementInfo}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 26.07.17
  */
object DataManagementHelper extends JsonSupport {

  def toDatabaseInfo(response: HttpResponse)(implicit reader: InfluxReader[DatabaseInfo], materializer: ActorMaterializer, ex: ExecutionContext): Future[Seq[DatabaseInfo]] = {
    unmarshalBody(response)
      .map(getInfluxValue)
      .map(_.map(reader.read).filterNot(_.dbName == "_internal"))
  }

  def toMeasurementInfo(response: HttpResponse)(implicit reader: InfluxReader[MeasurementInfo], materializer: ActorMaterializer, ex: ExecutionContext): Future[Seq[MeasurementInfo]] = {
    unmarshalBody(response)
      .map(getInfluxValue)
      .map(_.map(reader.read))
  }
}

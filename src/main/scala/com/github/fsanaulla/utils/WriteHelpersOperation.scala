package com.github.fsanaulla.utils

import akka.http.scaladsl.model.RequestEntity
import akka.stream.ActorMaterializer
import com.github.fsanaulla.model.{InfluxCredentials, Result}
import com.github.fsanaulla.query.DatabaseOperationQuery
import com.github.fsanaulla.utils.ResponseHandler.toResult
import com.github.fsanaulla.utils.constants.Consistencys.Consistency
import com.github.fsanaulla.utils.constants.Precisions.Precision

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 03.09.17
  */
private[fsanaulla] trait WriteHelpersOperation extends DatabaseOperationQuery with RequestBuilder with PointTransformer {

  def write(dbName: String,
            entity: RequestEntity,
            consistency: Consistency,
            precision: Precision,
            retentionPolicy: Option[String])
           (implicit credentials: InfluxCredentials,
            ex: ExecutionContext,
            mat: ActorMaterializer): Future[Result] = {
    buildRequest(
      uri = writeToInfluxQuery(dbName, consistency, precision, retentionPolicy),
      entity = entity
    ).flatMap(toResult)
  }
}

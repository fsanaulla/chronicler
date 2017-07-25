package com.fsanaulla.utils

import akka.http.scaladsl.model.HttpResponse
import akka.stream.ActorMaterializer
import com.fsanaulla.model.UserInfo.UserInfoInfluxReader
import com.fsanaulla.model.{InfluxReader, UserInfo}
import spray.json.{JsArray, JsObject}

import scala.concurrent.{ExecutionContext, Future}

trait UserManagementHelper extends JsonSupport {
  def toShowResult(response: HttpResponse)(implicit reader: InfluxReader[UserInfo],
                                           mat: ActorMaterializer,
                                           ex: ExecutionContext): Future[Seq[UserInfo]] = {
    unmarshalBody(response)
      .map(_.getFields("results").head.convertTo[Seq[JsObject]].head)
      .map(_.getFields("series").head.convertTo[Seq[JsObject]].head)
      .map(_.getFields("values").head.convertTo[Seq[JsArray]])
      .map(_.map(reader.read))
  }
}

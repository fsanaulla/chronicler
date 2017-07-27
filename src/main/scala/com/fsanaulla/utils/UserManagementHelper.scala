package com.fsanaulla.utils

import akka.http.scaladsl.model.HttpResponse
import akka.stream.ActorMaterializer
import com.fsanaulla.model.UserInfo.UserInfoInfluxReader
import com.fsanaulla.model.UserPrivilegesInfo.UserPrivilegesInfoInfluxReader
import com.fsanaulla.model.{InfluxReader, UserInfo, UserPrivilegesInfo}
import spray.json.{JsArray, JsObject}

import scala.concurrent.{ExecutionContext, Future}

object UserManagementHelper extends JsonSupport {

  def toUserInfo(response: HttpResponse)(implicit reader: InfluxReader[UserInfo], materializer: ActorMaterializer, ex: ExecutionContext): Future[Seq[UserInfo]] = {
    unmarshalBody(response)
      .map(getInfluxValue)
      .map(_.map(reader.read))
  }

  def toUserPrivilegesInfo(response: HttpResponse)(implicit reader: InfluxReader[UserPrivilegesInfo], materializer: ActorMaterializer, ex: ExecutionContext): Future[Seq[UserPrivilegesInfo]] = {
    unmarshalBody(response)
      .map(getInfluxValue)
      .map {
        case seq: Seq[JsArray] if seq.nonEmpty => seq.map(reader.read)
        case _ => Nil
      }
  }
}

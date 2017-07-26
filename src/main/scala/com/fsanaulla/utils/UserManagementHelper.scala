package com.fsanaulla.utils

import akka.http.scaladsl.model.HttpResponse
import akka.stream.ActorMaterializer
import com.fsanaulla.model.UserInfo.UserInfoInfluxReader
import com.fsanaulla.model.{InfluxReader, UserInfo, UserPrivilegesInfo}
import spray.json.{JsArray, JsObject}

import scala.concurrent.{ExecutionContext, Future}

trait UserManagementHelper extends JsonSupport {

  implicit val materializer: ActorMaterializer
  implicit val ex: ExecutionContext

  def toUserInfo(response: HttpResponse)(implicit reader: InfluxReader[UserInfo]): Future[Seq[UserInfo]] = {
    unmarshalBody(response)
      .map(getInfluxValue)
      .map(_.map(reader.read))
  }

  def toUserPrivilegesInfo(response: HttpResponse)(implicit reader: InfluxReader[UserPrivilegesInfo]): Future[Seq[UserPrivilegesInfo]] = {
    unmarshalBody(response)
      .map(getInfluxValue)
      .map(_.map(reader.read))
  }
}

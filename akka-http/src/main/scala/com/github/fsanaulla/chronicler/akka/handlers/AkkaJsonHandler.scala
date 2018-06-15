package com.github.fsanaulla.chronicler.akka.handlers

import _root_.akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import _root_.akka.http.scaladsl.unmarshalling.{Unmarshal, Unmarshaller}
import _root_.akka.stream.ActorMaterializer
import _root_.akka.util.ByteString
import com.github.fsanaulla.chronicler.core.handlers.JsonHandler
import com.github.fsanaulla.chronicler.core.model.Executable
import com.github.fsanaulla.chronicler.core.utils.Extensions.RichJValue
import jawn.ast.{JParser, JValue}

import scala.concurrent.Future

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 15.03.18
  */
private[akka] trait AkkaJsonHandler extends JsonHandler[Future, HttpResponse] with Executable {

  protected implicit val mat: ActorMaterializer

  /** Custom Unmarshaller for Jawn JSON */
  implicit val unm: Unmarshaller[HttpEntity, JValue] = {
    Unmarshaller.withMaterializer {
      implicit ex =>
        implicit mat =>
          entity: HttpEntity =>
            entity.dataBytes
              .runFold(ByteString.empty)(_ ++ _)
              .flatMap(db => Future.fromTry(JParser.parseFromString(db.utf8String)))
    }
  }

  override def getResponseBody(response: HttpResponse): Future[JValue] =
    Unmarshal(response.entity).to[JValue]

  override def getResponseError(response: HttpResponse): Future[String] =
    getResponseBody(response).map(_.get("error").asString)

  override def getOptResponseError(response: HttpResponse): Future[Option[String]] =
    getResponseBody(response)
      .map(_.get("results").arrayValue.flatMap(_.headOption))
      .map(_.flatMap(_.get("error").getString))
}

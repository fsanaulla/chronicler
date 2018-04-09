package com.github.fsanaulla.chronicler.akka.handlers

import _root_.akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import _root_.akka.http.scaladsl.unmarshalling.{Unmarshal, Unmarshaller}
import _root_.akka.stream.ActorMaterializer
import akka.util.ByteString
import com.github.fsanaulla.core.handlers.json.JsonHandler
import jawn.ast.{JParser, JValue}

import scala.concurrent.Future

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 15.03.18
  */
private[fsanaulla] trait AkkaJsonHandler extends JsonHandler[HttpResponse] {

  protected implicit val mat: ActorMaterializer

  /**
    * Custom Unmarshaller for Jawn JSON
    */
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


  override def getJsBody(response: HttpResponse): Future[JValue] = {
    Unmarshal(response.entity).to[JValue]
  }

}

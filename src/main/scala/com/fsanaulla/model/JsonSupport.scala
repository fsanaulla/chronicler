package com.fsanaulla.model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol


/**
  * Created by fayaz on 12.07.17.
  */
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  import spray.json._
}

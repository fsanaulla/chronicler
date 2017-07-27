package com.fsanaulla.model

import spray.json.{DeserializationException, JsArray, JsBoolean, JsString}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.07.17
  */
case class MeasurementInfo(name: String)

object MeasurementInfo {
  implicit object MeasurementInfoInfluxReader extends InfluxReader[MeasurementInfo] {
    override def read(js: JsArray): MeasurementInfo = js.elements match {
      case Vector(JsString(name)) => MeasurementInfo(name)
      case _ => throw DeserializationException(s"Can't deserialize $UserInfo object")
    }
  }
}

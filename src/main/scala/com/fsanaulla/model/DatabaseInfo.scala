package com.fsanaulla.model

import spray.json.{DeserializationException, JsArray, JsBoolean, JsString}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 26.07.17
  */
case class DatabaseInfo(dbName: String)

object DatabaseInfo {
  implicit object DatabaseInfoInfluxReader extends InfluxReader[DatabaseInfo] {
    override def read(js: JsArray): DatabaseInfo = js.elements match {
      case Vector(JsString(dbName)) => DatabaseInfo(dbName)
      case _ => throw DeserializationException(s"Can't deserialize $UserInfo object")
    }
  }
}

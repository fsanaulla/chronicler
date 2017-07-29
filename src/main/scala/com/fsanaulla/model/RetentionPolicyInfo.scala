package com.fsanaulla.model

import spray.json.{DeserializationException, JsArray, JsBoolean, JsNumber, JsString}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 29.07.17
  */
case class RetentionPolicyInfo(name: String,
                               duration: String,
                               shardGroupDuration: String,
                               replication: Int,
                               default: Boolean)

object RetentionPolicyInfo {
  implicit object RetentionPolicyInfluxReader extends InfluxReader[RetentionPolicyInfo] {
    override def read(js: JsArray): RetentionPolicyInfo = js.elements match {
      case Vector(JsString(name), JsString(duration), JsString(shardGroupdDuration), JsNumber(replication), JsBoolean(default)) => RetentionPolicyInfo(name, duration, shardGroupdDuration, replication.toInt, default)
      case _ => throw DeserializationException(s"Can't deserialize $RetentionPolicyInfo object")
    }
  }
}
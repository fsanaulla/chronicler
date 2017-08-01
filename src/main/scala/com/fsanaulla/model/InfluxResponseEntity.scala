package com.fsanaulla.model

import spray.json.{DeserializationException, JsArray, JsBoolean, JsNumber, JsString}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 30.07.17
  */
sealed trait InfoResponseEntity

case class UserInfo(username: String, isAdmin: Boolean) extends InfoResponseEntity

case class UserPrivilegesInfo(database: String, privilege: String) extends InfoResponseEntity

case class DatabaseInfo(dbName: String) extends InfoResponseEntity

case class MeasurementInfo(name: String) extends InfoResponseEntity

case class RetentionPolicyInfo(name: String,
                               duration: String,
                               shardGroupDuration: String,
                               replication: Int,
                               default: Boolean) extends InfoResponseEntity

object RetentionPolicyInfo {

  implicit object RetentionPolicyInfluxReader extends InfluxReader[RetentionPolicyInfo] {
    override def read(js: JsArray): RetentionPolicyInfo = js.elements match {
      case Vector(JsString(name), JsString(duration), JsString(shardGroupdDuration), JsNumber(replication), JsBoolean(default)) =>
        RetentionPolicyInfo(name, duration, shardGroupdDuration, replication.toInt, default)
      case _ => throw DeserializationException(s"Can't deserialize $RetentionPolicyInfo object")
    }
  }

}

object MeasurementInfo {

  implicit object MeasurementInfoInfluxReader extends InfluxReader[MeasurementInfo] {
    override def read(js: JsArray): MeasurementInfo = js.elements match {
      case Vector(JsString(name)) => MeasurementInfo(name)
      case _ => throw DeserializationException(s"Can't deserialize $UserInfo object")
    }
  }

}

object DatabaseInfo {

  implicit object DatabaseInfoInfluxReader extends InfluxReader[DatabaseInfo] {
    override def read(js: JsArray): DatabaseInfo = js.elements match {
      case Vector(JsString(dbName)) => DatabaseInfo(dbName)
      case _ => throw DeserializationException(s"Can't deserialize $UserInfo object")
    }
  }

}

object UserInfo {

  implicit object UserInfoInfluxReader extends InfluxReader[UserInfo] {
    override def read(js: JsArray): UserInfo = js.elements match {
      case Vector(JsString(username), JsBoolean(admin)) => UserInfo(username, admin)
      case _ => throw DeserializationException(s"Can't deserialize $UserInfo object")
    }
  }

}

object UserPrivilegesInfo {

  implicit object UserPrivilegesInfoInfluxReader extends InfluxReader[UserPrivilegesInfo] {
    override def read(js: JsArray): UserPrivilegesInfo = js.elements match {
      case Vector(JsString(username), JsString(admin)) => UserPrivilegesInfo(username, admin)
      case _ => throw DeserializationException(s"Can't deserialize $UserPrivilegesInfo object")
    }
  }

}

object JsArrayReadeable {

  implicit object JsArrayInfluxReader extends InfluxReader[JsArray] {
    override def read(js: JsArray): JsArray = js
  }

}

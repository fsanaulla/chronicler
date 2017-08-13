package com.fsanaulla.model

import spray.json.{DeserializationException, JsArray, JsBoolean, JsNumber, JsObject, JsString}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 30.07.17
  */
object InfluxImplicits {

  sealed trait InfoResponseEntity

  case class UserInfo(username: String, isAdmin: Boolean) extends InfoResponseEntity

  case class UserPrivilegesInfo(database: String, privilege: String) extends InfoResponseEntity

  case class RetentionPolicyInfo(name: String,
                                 duration: String,
                                 shardGroupDuration: String,
                                 replication: Int,
                                 default: Boolean) extends InfoResponseEntity

  case class ContinuousQuery(cqName: String, query: String)

  case class ContinuousQueryInfo(dbName: String, querys: Seq[ContinuousQuery]) extends InfoResponseEntity

  implicit object StringInfluxReader extends InfluxReader[String] {
    override def read(js: JsArray): String = js.elements match {
      case Vector(JsString(name)) => name
      case _ => throw DeserializationException(s"Can't deserialize $js to String")
    }
  }

  implicit object RetentionPolicyInfluxReader extends InfluxReader[RetentionPolicyInfo] {
    override def read(js: JsArray): RetentionPolicyInfo = js.elements match {
      case Vector(JsString(name), JsString(duration), JsString(shardGroupdDuration), JsNumber(replication), JsBoolean(default)) =>
        RetentionPolicyInfo(name, duration, shardGroupdDuration, replication.toInt, default)
      case _ => throw DeserializationException(s"Can't deserialize $RetentionPolicyInfo object")
    }
  }

  implicit object UserInfoInfluxReader extends InfluxReader[UserInfo] {
    override def read(js: JsArray): UserInfo = js.elements match {
      case Vector(JsString(username), JsBoolean(admin)) => UserInfo(username, admin)
      case _ => throw DeserializationException(s"Can't deserialize $UserInfo object")
    }
  }


  implicit object UserPrivilegesInfoInfluxReader extends InfluxReader[UserPrivilegesInfo] {
    override def read(js: JsArray): UserPrivilegesInfo = js.elements match {
      case Vector(JsString(username), JsString(admin)) => UserPrivilegesInfo(username, admin)
      case _ => throw DeserializationException(s"Can't deserialize $UserPrivilegesInfo object")
    }
  }


  implicit object ContinuousQueryInfluxReader extends InfluxReader[ContinuousQuery] {
    override def read(js: JsArray): ContinuousQuery = js.elements match {
      case Vector(JsString(cqName), JsString(query)) => ContinuousQuery(cqName, query)
      case _ => throw DeserializationException(s"Can't deserialize $UserPrivilegesInfo object")
    }
  }
}

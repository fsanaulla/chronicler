package com.github.fsanaulla.core.utils

import com.github.fsanaulla.core.enums.{Destinations, Privileges}
import com.github.fsanaulla.core.model._
import com.github.fsanaulla.core.utils.PrimitiveJawnImplicits._
import jawn.ast.{JArray, JValue}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 30.07.17
  */
object DefaultInfluxImplicits {

  implicit object StringInfluxReader extends InfluxReader[String] {
    def read(js: JArray): String = js.vs match {
      case Array(str: JValue) => str
      case _ =>
        throw new DeserializationException(s"Can't deserialize $js to String")
    }
  }

  implicit object IntInfluxReader extends InfluxReader[Int] {
    def read(js: JArray): Int = js.vs match {
      case Array(js: JValue) => js
      case _ => throw new DeserializationException(s"Can't deserialize $js to Int")
    }
  }

  implicit object DoubleInfluxReader extends InfluxReader[Double] {
    def read(js: JArray): Double = js.vs match {
      case Array(js: JValue) => js
      case _ =>
        throw new DeserializationException(s"Can't deserialize $js to Double")
    }
  }

  implicit object LongInfluxReader extends InfluxReader[Long] {
    def read(js: JArray): Long = js.vs match {
      case Array(js: JValue) => js
      case _ =>
        throw new DeserializationException(s"Can't deserialize $js to Long")
    }
  }

  implicit object BooleanInfluxReader extends InfluxReader[Boolean] {
    def read(js: JArray): Boolean = js.vs match {
      case Array(js: JValue) => js
      case _ =>
        throw new DeserializationException(s"Can't deserialize $js to Boolean")
    }
  }

  implicit object RetentionPolicyInfluxReader extends InfluxReader[RetentionPolicyInfo] {
    def read(js: JArray): RetentionPolicyInfo = js.vs match {
      case Array(name: JValue, duration: JValue, shardGroupDuration: JValue, replication: JValue, default: JValue) =>
        RetentionPolicyInfo(name, duration, shardGroupDuration, replication, default)
      case _ =>
        throw new DeserializationException(s"Can't deserialize $RetentionPolicyInfo object")
    }
  }

  implicit object UserInfoInfluxReader extends InfluxReader[UserInfo] {
    override def read(js: JArray): UserInfo = js.vs match {
      case Array(username: JValue, admin: JValue) =>
        UserInfo(username, admin)
      case _ =>
        throw new DeserializationException(s"Can't deserialize $UserInfo object")
    }
  }

  implicit object UserPrivilegesInfoInfluxReader extends InfluxReader[UserPrivilegesInfo] {
    override def read(js: JArray): UserPrivilegesInfo = js.vs match {
      case Array(username: JValue, admin: JValue) =>
        UserPrivilegesInfo(username, Privileges.withName(admin))
      case _ =>
        throw new DeserializationException(s"Can't deserialize $UserPrivilegesInfo object")
    }
  }

  implicit object ContinuousQueryInfluxReader extends InfluxReader[ContinuousQuery] {
    override def read(js: JArray): ContinuousQuery = js.vs match {
      case Array(cqName: JValue, query: JValue) =>
        ContinuousQuery(cqName, query)
      case _ =>
        throw new DeserializationException(s"Can't deserialize $ContinuousQuery object")
    }
  }

  implicit object ShardInfluxReader extends InfluxReader[Shard] {
    override def read(js: JArray): Shard = js.vs match {
      case Array(shardId: JValue, dbName: JValue, rpName: JValue, shardGroupId: JValue, startTime: JValue, endTime: JValue, expiryTime: JValue, owners: JValue) =>
        Shard(shardId, dbName, rpName, shardGroupId, startTime, endTime, expiryTime, owners)
      case _ =>
        throw new DeserializationException(s"Can't deserialize $Shard object")
    }
  }

  implicit object QueryInfoInfluxReader extends InfluxReader[QueryInfo] {
    override def read(js: JArray): QueryInfo = js.vs match {
      case Array(queryId: JValue, query: JValue, dbName: JValue, duration: JValue) =>
        QueryInfo(queryId, query, dbName, duration)
      case _ =>
        throw new DeserializationException(s"Can't deserialize $QueryInfo object")
    }
  }

  implicit object ShardGroupInfluxReader extends InfluxReader[ShardGroup] {
    override def read(js: JArray): ShardGroup = js.vs match {
      case Array(shardId: JValue, dbName: JValue, rpName: JValue, startTime: JValue, endTime: JValue, expiryTime: JValue) =>
        ShardGroup(shardId, dbName, rpName, startTime, endTime, expiryTime)
      case _ =>
        throw new DeserializationException(s"Can't deserialize $ShardGroup object")
    }
  }

  implicit object SubscriptionInfluxReader extends InfluxReader[Subscription] {
    override def read(js: JArray): Subscription = js.vs match {
      case Array(rpName: JValue, subsName: JValue, destType: JValue, JArray(elems)) =>
        Subscription(rpName, subsName, Destinations.withName(destType), elems.map(_.asString))
      case _ =>
        throw new DeserializationException(s"Can't deserialize $Subscription object")
    }
  }

  implicit object FieldInfoInfluxReader extends InfluxReader[FieldInfo] {
    override def read(js: JArray): FieldInfo = js.vs match {
      case Array(fieldName: JValue, fieldType: JValue) =>
        FieldInfo(fieldName, fieldType)
      case _ =>
        throw new DeserializationException(s"Can't deserialize $FieldInfo object")
    }
  }

  implicit object TagValueInfluxReader extends InfluxReader[TagValue] {
    override def read(js: JArray): TagValue = js.vs match {
      case Array(tag: JValue, value: JValue) =>
        TagValue(tag, value)
      case _ =>
        throw new DeserializationException(s"Can't deserialize $TagValue object")
    }
  }
}

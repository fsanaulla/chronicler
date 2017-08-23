package com.fsanaulla.model

import com.fsanaulla.utils.JsonSupport._
import spray.json.{DeserializationException, JsArray, JsBoolean, JsNumber, JsObject, JsString}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 30.07.17
  */
object InfluxImplicits {

  implicit def bigDec2Int(bd: BigDecimal): Int = bd.toInt

  implicit object StringInfluxReader extends InfluxReader[String] {
    override def read(js: JsArray): String = js.elements match {
      case Vector(JsString(str)) => str
      case _ => throw DeserializationException(s"Can't deserialize $js to String")
    }
  }

  implicit object IntInfluxReader extends InfluxReader[Int] {
    override def read(js: JsArray): Int = js.elements match {
      case Vector(JsNumber(num)) => num
      case _ => throw DeserializationException(s"Can't deserialize $js to Int")
    }
  }

  implicit object DoubleInfluxReader extends InfluxReader[Double] {
    override def read(js: JsArray): Double = js.elements match {
      case Vector(JsNumber(num)) => num.toDouble
      case _ => throw DeserializationException(s"Can't deserialize $js to Double")
    }
  }

  implicit object LongInfluxReader extends InfluxReader[Long] {
    override def read(js: JsArray): Long = js.elements match {
      case Vector(JsNumber(num)) => num.toLong
      case _ => throw DeserializationException(s"Can't deserialize $js to Long")
    }
  }

  implicit object BooleanInfluxReader extends InfluxReader[Boolean] {
    override def read(js: JsArray): Boolean = js.elements match {
      case Vector(JsBoolean(bool)) => bool
      case _ => throw DeserializationException(s"Can't deserialize $js to Boolean")
    }
  }

  implicit object RetentionPolicyInfluxReader extends InfluxReader[RetentionPolicyInfo] {
    override def read(js: JsArray): RetentionPolicyInfo = js.elements match {
      case Vector(JsString(name),
                  JsString(duration),
                  JsString(shardGroupdDuration),
                  JsNumber(replication),
                  JsBoolean(default)) => RetentionPolicyInfo(name, duration, shardGroupdDuration, replication, default)
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
      case _ => throw DeserializationException(s"Can't deserialize $ContinuousQuery object")
    }
  }

  implicit object ShardInfluxReader extends InfluxReader[Shard] {
    override def read(js: JsArray): Shard = js.elements match {
      case Vector(JsNumber(shardId),
                  JsString(dbName),
                  JsString(rpName),
                  JsNumber(shardGroupId),
                  JsString(startTime),
                  JsString(endTime),
                  JsString(expiryTime),
                  JsString(owners)) => Shard(shardId.toInt, dbName, rpName, shardGroupId, startTime, endTime, expiryTime, owners)
      case _ => throw DeserializationException(s"Can't deserialize $Shard object")
    }
  }

  implicit object QueryInfoInfluxReader extends InfluxReader[QueryInfo] {
    override def read(js: JsArray): QueryInfo = js.elements match {
      case Vector(JsNumber(queryId), JsString(query), JsString(dbName), JsString(duration)) =>
        QueryInfo(queryId, query, dbName, duration)
      case _ => throw DeserializationException(s"Can't deserialize $QueryInfo object")
    }
  }

  implicit object ShardGroupInfluxReader extends InfluxReader[ShardGroup] {
    override def read(js: JsArray): ShardGroup = js.elements match {
      case Vector(JsNumber(shardId),
                  JsString(dbName),
                  JsString(rpName),
                  JsString(startTime),
                  JsString(endTime),
                  JsString(expiryTime)) => ShardGroup(shardId, dbName, rpName, startTime, endTime, expiryTime)
      case _ => throw DeserializationException(s"Can't deserialize $ShardGroup object")
    }
  }

  implicit object SubscriptionInfluxReader extends InfluxReader[Subscription] {
    override def read(js: JsArray): Subscription = js.elements match {
      case Vector(JsString(rpName), JsString(subsName), JsString(destType), JsArray(elems)) =>
        Subscription(rpName, subsName, destType, elems.map(_.convertTo[String]))
      case _ => throw DeserializationException(s"Can't deserialize $Subscription object")
    }
  }

  implicit object FieldInfoInfluxReader extends InfluxReader[FieldInfo] {
    override def read(js: JsArray): FieldInfo = js.elements match {
      case Vector(JsString(fieldName), JsString(fieldType)) => FieldInfo(fieldName, fieldType)
      case _ => throw DeserializationException(s"Can't deserialize $FieldInfo object")
    }
  }

  implicit object TagValueInfluxReader extends InfluxReader[TagValue] {
    override def read(js: JsArray): TagValue = js.elements match {
      case Vector(JsString(tag), JsString(value)) => TagValue(tag, value)
      case _ => throw DeserializationException(s"Can't deserialize $TagValue object")
    }
  }
}

/*
 * Copyright 2017-2019 Faiaz Sanaulla
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.fsanaulla.chronicler.core

import _root_.jawn.ast.{JArray, JValue}
import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.enums.{Destinations, Privileges}
import com.github.fsanaulla.chronicler.core.jawn._
import com.github.fsanaulla.chronicler.core.model._

package object implicits {

  private[this] def exception(msg: String) = new ParsingException(msg)

  implicit final class RichString(private val str: String) extends AnyVal {
    def escapeKey: String = str.replaceAll("([ ,=])", "\\\\$1")
    def escapeMeas: String = str.replaceAll("([ ,])", "\\\\$1")
  }

  implicit object StringInfluxReader extends InfluxReader[String] {
    def read(js: JArray): ErrorOr[String] = js.vs match {
      case Array(str: JValue) =>
        Right(str)
      case _                  =>
        Left(exception(s"Can't deserialize $js to String"))
    }
  }

  implicit object IntInfluxReader extends InfluxReader[Int] {
    def read(js: JArray): ErrorOr[Int] = js.vs match {
      case Array(js: JValue) =>
        Right(js)
      case _                 =>
        Left(exception(s"Can't deserialize $js to Int"))
    }
  }

  implicit object DoubleInfluxReader extends InfluxReader[Double] {
    def read(js: JArray): ErrorOr[Double] = js.vs match {
      case Array(js: JValue) =>
        Right(js)
      case _ =>
        Left(exception(s"Can't deserialize $js to Double"))
    }
  }

  implicit object LongInfluxReader extends InfluxReader[Long] {
    def read(js: JArray): ErrorOr[Long] = js.vs match {
      case Array(js: JValue) =>
        Right(js)
      case _ =>
        Left(exception(s"Can't deserialize $js to Long"))
    }
  }

  implicit object BooleanInfluxReader extends InfluxReader[Boolean] {
    def read(js: JArray): ErrorOr[Boolean] = js.vs match {
      case Array(js: JValue) =>
        Right(js)
      case _ =>
        Left(exception(s"Can't deserialize $js to Boolean"))
    }
  }

  implicit object RetentionPolicyInfluxReader extends InfluxReader[RetentionPolicyInfo] {
    def read(js: JArray): ErrorOr[RetentionPolicyInfo] = js.vs match {
      case Array(name: JValue, duration: JValue, shardGroupDuration: JValue, replication: JValue, default: JValue) =>
        Right(RetentionPolicyInfo(name, duration, shardGroupDuration, replication, default))
      case _ =>
        Left(exception(s"Can't deserialize RetentionPolicyInfo object"))
    }
  }

  implicit object UserInfoInfluxReader extends InfluxReader[UserInfo] {
    override def read(js: JArray): ErrorOr[UserInfo] = js.vs match {
      case Array(username: JValue, admin: JValue) =>
        Right(UserInfo(username, admin))
      case _ =>
        Left(exception(s"Can't deserialize $UserInfo object"))
    }
  }

  implicit object UserPrivilegesInfoInfluxReader extends InfluxReader[UserPrivilegesInfo] {
    override def read(js: JArray): ErrorOr[UserPrivilegesInfo] = js.vs match {
      case Array(username: JValue, privilege: JValue) =>
        Privileges.withNameOption(privilege).fold[ErrorOr[UserPrivilegesInfo]](
          Left(new IllegalArgumentException(s"Unsupported privilege: $privilege"))
        )(p => Right(UserPrivilegesInfo(username, p)))
      case _ =>
        Left(exception(s"Can't deserialize $UserPrivilegesInfo object"))
    }
  }

  implicit object ContinuousQueryInfluxReader extends InfluxReader[ContinuousQuery] {
    override def read(js: JArray): ErrorOr[ContinuousQuery] = js.vs match {
      case Array(cqName: JValue, query: JValue) =>
        Right(ContinuousQuery(cqName, query))
      case _ =>
        Left(exception(s"Can't deserialize $ContinuousQuery object"))
    }
  }

  implicit object ShardInfluxReader extends InfluxReader[Shard] {
    override def read(js: JArray): ErrorOr[Shard] = js.vs match {
      case Array(shardId: JValue, dbName: JValue, rpName: JValue, shardGroupId: JValue, startTime: JValue, endTime: JValue, expiryTime: JValue, owners: JValue) =>
        Right(Shard(shardId, dbName, rpName, shardGroupId, startTime, endTime, expiryTime, owners))
      case _ =>
        Left(exception(s"Can't deserialize $Shard object"))
    }
  }

  implicit object QueryInfoInfluxReader extends InfluxReader[QueryInfo] {
    override def read(js: JArray): ErrorOr[QueryInfo] = js.vs match {
      case Array(queryId: JValue, query: JValue, dbName: JValue, duration: JValue) =>
        Right(QueryInfo(queryId, query, dbName, duration))
      case _ =>
        Left(exception(s"Can't deserialize $QueryInfo object"))
    }
  }

  implicit object ShardGroupInfluxReader extends InfluxReader[ShardGroup] {
    override def read(js: JArray): ErrorOr[ShardGroup] = js.vs match {
      case Array(shardId: JValue, dbName: JValue, rpName: JValue, startTime: JValue, endTime: JValue, expiryTime: JValue) =>
        Right(ShardGroup(shardId, dbName, rpName, startTime, endTime, expiryTime))
      case _ =>
        Left(exception(s"Can't deserialize $ShardGroup object"))
    }
  }

  implicit object SubscriptionInfluxReader extends InfluxReader[Subscription] {
    override def read(js: JArray): ErrorOr[Subscription] = js.vs match {
      case Array(rpName: JValue, subsName: JValue, destType: JValue, JArray(elems)) =>
        Destinations
          .withNameOption(destType)
          .fold[ErrorOr[Subscription]](Left(new IllegalArgumentException(s"Unsupported destination type: $destType"))) {
            d => Right(Subscription(rpName, subsName, d, elems.map(_.asString)))
        }
      case _ =>
        Left(exception(s"Can't deserialize $Subscription object"))
    }
  }

  implicit object FieldInfoInfluxReader extends InfluxReader[FieldInfo] {
    override def read(js: JArray): ErrorOr[FieldInfo] = js.vs match {
      case Array(fieldName: JValue, fieldType: JValue) =>
        Right(FieldInfo(fieldName, fieldType))
      case _ =>
        Left(exception(s"Can't deserialize FieldInfo object"))
    }
  }

  implicit object TagValueInfluxReader extends InfluxReader[TagValue] {
    override def read(js: JArray): ErrorOr[TagValue] = js.vs match {
      case Array(tag: JValue, value: JValue) =>
        Right(TagValue(tag, value))
      case _ =>
        Left(exception(s"Can't deserialize TagValue object"))
    }
  }
}

package com.github.fsanaulla.chronicler.core.management.rp

import org.typelevel.jawn.ast.{JArray, JValue}
import com.github.fsanaulla.chronicler.core.model.InfluxReader
import com.github.fsanaulla.chronicler.core.jawn._
import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.model.ParsingException

final case class RetentionPolicyInfo(
    name: String,
    duration: String,
    shardGroupDuration: String,
    replication: Int,
    default: Boolean
)

object RetentionPolicyInfo {
  implicit val reader: InfluxReader[RetentionPolicyInfo] = new InfluxReader[RetentionPolicyInfo] {

    def read(js: JArray): ErrorOr[RetentionPolicyInfo] = js.vs match {
      case Array(
          name: JValue,
          duration: JValue,
          shardGroupDuration: JValue,
          replication: JValue,
          default: JValue
          ) =>
        Right(RetentionPolicyInfo(name, duration, shardGroupDuration, replication, default))
      case _ =>
        Left(new ParsingException(s"Can't deserialize RetentionPolicyInfo object"))
    }

    override def readUnsafe(js: JArray): RetentionPolicyInfo = js.vs match {
      case Array(
          name: JValue,
          duration: JValue,
          shardGroupDuration: JValue,
          replication: JValue,
          default: JValue
          ) =>
        RetentionPolicyInfo(name, duration, shardGroupDuration, replication, default)
      case _ =>
        throw new ParsingException(s"Can't deserialize RetentionPolicyInfo object")
    }
  }
}

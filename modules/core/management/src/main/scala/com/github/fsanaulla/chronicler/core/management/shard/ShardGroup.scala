package com.github.fsanaulla.chronicler.core.management.shard

import org.typelevel.jawn.ast.{JArray, JValue}
import com.github.fsanaulla.chronicler.core.model.InfluxReader
import com.github.fsanaulla.chronicler.core.jawn._
import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.model.ParsingException

final case class ShardGroup(
    id: Int,
    dbName: String,
    rpName: String,
    startTime: String,
    endTime: String,
    expiryTime: String
)

object ShardGroup {
  implicit val reader: InfluxReader[ShardGroup] = new InfluxReader[ShardGroup] {
    override def read(js: JArray): ErrorOr[ShardGroup] = js.vs match {
      case Array(
          shardId: JValue,
          dbName: JValue,
          rpName: JValue,
          startTime: JValue,
          endTime: JValue,
          expiryTime: JValue
          ) =>
        Right(ShardGroup(shardId, dbName, rpName, startTime, endTime, expiryTime))
      case _ =>
        Left(new ParsingException(s"Can't deserialize $ShardGroup object"))
    }

    override def readUnsafe(js: JArray): ShardGroup = js.vs match {
      case Array(
          shardId: JValue,
          dbName: JValue,
          rpName: JValue,
          startTime: JValue,
          endTime: JValue,
          expiryTime: JValue
          ) =>
        ShardGroup(shardId, dbName, rpName, startTime, endTime, expiryTime)
      case _ =>
        throw new ParsingException(s"Can't deserialize $ShardGroup object")
    }
  }
}

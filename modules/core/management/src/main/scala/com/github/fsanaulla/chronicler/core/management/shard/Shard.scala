package com.github.fsanaulla.chronicler.core.management.shard

import org.typelevel.jawn.ast.{JArray, JValue}
import com.github.fsanaulla.chronicler.core.model.InfluxReader
import com.github.fsanaulla.chronicler.core.jawn._
import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.model.ParsingException

final case class Shard(
    id: Int,
    dbName: String,
    rpName: String,
    shardGroup: Int,
    startTime: String,
    endTime: String,
    expiryTime: String,
    owners: String
)

object Shard {

  implicit val reader: InfluxReader[Shard] = new InfluxReader[Shard] {
    override def read(js: JArray): ErrorOr[Shard] = js.vs match {
      case Array(
          shardId: JValue,
          dbName: JValue,
          rpName: JValue,
          shardGroupId: JValue,
          startTime: JValue,
          endTime: JValue,
          expiryTime: JValue,
          owners: JValue
          ) =>
        Right(Shard(shardId, dbName, rpName, shardGroupId, startTime, endTime, expiryTime, owners))
      case _ =>
        Left(new ParsingException(s"Can't deserialize $Shard object"))
    }

    override def readUnsafe(js: JArray): Shard = js.vs match {
      case Array(
          shardId: JValue,
          dbName: JValue,
          rpName: JValue,
          shardGroupId: JValue,
          startTime: JValue,
          endTime: JValue,
          expiryTime: JValue,
          owners: JValue
          ) =>
        Shard(shardId, dbName, rpName, shardGroupId, startTime, endTime, expiryTime, owners)
      case _ =>
        throw new ParsingException(s"Can't deserialize $Shard object")
    }
  }
}

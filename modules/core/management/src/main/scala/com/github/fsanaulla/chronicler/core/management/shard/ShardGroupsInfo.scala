package com.github.fsanaulla.chronicler.core.management.shard

import org.typelevel.jawn.ast.{JArray, JValue}
import com.github.fsanaulla.chronicler.core.model.InfluxReader
import com.github.fsanaulla.chronicler.core.jawn._
import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.model.ParsingException

final case class ShardGroupsInfo(shardGroupName: String, shardGroups: Array[ShardGroup])

object ShardGroupsInfo {

}

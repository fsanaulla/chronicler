package com.github.fsanaulla.model

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
case class UserInfo(username: String, isAdmin: Boolean)

case class UserPrivilegesInfo(database: String, privilege: String)

case class RetentionPolicyInfo(name: String,
                               duration: String,
                               shardGroupDuration: String,
                               replication: Int,
                               default: Boolean)

case class ContinuousQuery(cqName: String, query: String)

case class ShardGroup(id: Int,
                      dbName: String,
                      rpName: String,
                      startTime: String,
                      endTime: String,
                      expiryTime: String)

case class Shard(id: Int,
                 dbName: String,
                 rpName: String,
                 shardGroup: Int,
                 startTime: String,
                 endTime: String,
                 expiryTime: String,
                 owners: String)

case class Subscription(rpName: String, subsName: String, destType: String, addresses: Seq[String])

case class QueryInfo(queryId: Int, query: String, dbName: String, duration: String)

case class ContinuousQueryInfo(dbName: String, querys: Seq[ContinuousQuery])

case class ShardGroupsInfo(shardGroupName: String, seq: Seq[ShardGroup])

case class ShardInfo(dbName: String, shards: Seq[Shard])

case class SubscriptionInfo(dbName: String, subscriptions: Seq[Subscription])

case class FieldInfo(fieldName: String, fieldType: String)

case class TagValue(tag: String, value: String)

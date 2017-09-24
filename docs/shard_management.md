# Shard management <a name="shardManagement"></a>
Drop shard by id:
```
influx.dropShard(4)
res0: Future[Result]
```
Show shards group:
```
influx.showShardGroups()
res0: Future[QueryResult[ShardGroupsInfo]]
```
Show shards:
```
influx.showShards()
res0: Future[QueryResult[ShardInfo]]
```
Get database related shards:
```
influx.getShards("someDB")
res0: Future[QueryResult[Shard]]
```
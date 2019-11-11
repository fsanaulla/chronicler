# Retention Policy management <a name="rpManagement"></a>
In this section described [retention policy](https://docs.influxdata.com/influxdb/v1.3/query_language/database_management/)(further RP) operation
Create RP:
```
influx.createRetentionPolicy("rpName", "dbName", "duration", intReplicationCount, "optShardDuration", defaulfBool)
res0: Future[Result] 
```
Show database related RP's:
```
influx.showRetentionPolicies("dbName")
res0: Future[QueryResult[RetentionPolicyInfo]]
```
Drop RP:
```
influx.dropRetentionPolicy("rpName", "dbName")
res0: Future[Result]
```
Update RP:
```
influx.updateRetentionPolicy("rpName", "dbName", "duration", intReplicationCount, "optShardDuration", defaulfBool)
res0: Future[Result]
```

# Database management operations
In this section described [database management](https://docs.influxdata.com/influxdb/v1.3/query_language/database_management/) operation:

You can create db with such signatures
```
// With database name
influx.createDatabase("db")
res0: Future[Result]

// and with duration
influx.createDatabase("db", Some("2h))
res0: Future[Result]

// and with replication
influx.createDatabase("db", Some("2h"), Some(1))
res0: Future[Result]

// and with shard duration
influx.createDatabase("db", Some("2h"), Some(1), Some("2h"))
res0: Future[Result]

// and with associated retention policy name
influx.createDatabase("db", Some("2h"), Some(1), Some("2h"), Some("my_retention_policy"))
res0: Future[Result]
```

To choose needed database:
```
val db: Database = influx.use("mydb")
```
Drop database
```
influx.dropDatabase("db_name")
res0: Future[Result]
```
Drop measurement
```
influx.dropMeasurement("measurement_name")
res0: Future[Result]
```
Drop shard bu shardId
```
influx.dropShard(2)
res0: Future[Result]
```
Show database measurement
```
influx.showMeasurement("db_name")
res0: Future[QueryResult[String]]
```
Show databases:
```
influx.showDatabase()
res0: Future[QueryResult[String]]
```
Show measurement tag keys, `whereClause` it's simply predicate to filtering like `"bar > 4"`. `whereClause`, `limit`, `offset` are optional parameters.
```
influx.showTagKeys("database", "measurement", "whereClause", optLimit, optOffset)
res0: Future[QueryResult[String]]
```
Show measurement tag value's, `whereClause` it's simply predicate to filtering like `"bar > 4"`. `whereClause`, `limit`, `offset` are optional parameters.
```
influx.showTagValues("db_name", "measuremetn_name", Seq("key1", "key2"), whereClause, limit, offset)
res0: Future[QueryResult[TagValue]]
```
Show field keys:
```
influx.showFieldKeys("db_name", "measuremetn_name")
res0: Future[QueryResult[FieldInfo]]
```
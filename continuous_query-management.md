# Continuously Query management <a name="CQManagement"></a>
Main [CQ management](https://docs.influxdata.com/influxdb/v1.3/query_language/continuous_queries/#continuous-query-management)
To create Continuously Query(further CQ). Where query params look's like `SELECT count("bees") AS "count_bees" INTO "aggregate_bees" FROM "farm" GROUP BY time(30m)`:
```
influx.createCQ("dbName", "cqName", "query")
res0: Future[Result]
```
Dropping Continuously Query:
```
influx.dropCQ("dbName", "cqName")
res0: Future[Result]
```
Show Continuously Query's:
```
influx.showCQs()
res0: Future[QueryResult[ContinuousQueryInfo]]
```
Show database related Continuously Query's:
```
influx.showCQ("dbName")
res0: Future[QueryResult[ContinuousQuery]]
```
There is no default update method for Continuously Query, so `updateCQ` it's simulation using `dropCQ` and `createCQ` methods.
```
influx.updateCQ("dbName", "cqName", "query")
res0: Future[Result]
```
# Chronicler [![Build Status](https://travis-ci.org/fsanaulla/chronicler.svg?branch=master)](https://travis-ci.org/fsanaulla/chronicler) [![Coverage Status](https://coveralls.io/repos/github/fsanaulla/chronicler/badge.svg?branch=master)](https://coveralls.io/github/fsanaulla/chronicler?branch=master)

Chronicler - asynchronous [Scala](https://www.scala-lang.org/) client library for [InfluxDB](https://www.influxdata.com/) based on [Akka HTTP](http://doc.akka.io/docs/akka-http/current/scala/http/).

# Table of content
- [Versions](#version)
- [Usage](#usage)
- [Connection](#connection)
    - [Integration](#integration)
    - [Imports](#import)
    - [Create connection](#createConn)
- [Helper Tools](#helptools)
- [Time](#time)
- [Synchronize](#sync)
- [Response Handling](#resp)
- [Database management](#dbManagement)
- [Read and Write operation](#readWrite)
    - [Read operation](#read)
    - [Write operation](docs/write_operation_notes.md)
- [User management](#userManagement)
- [Continuously Query management](#CQManagement)
- [Subscription management](#subsManagement)
- [Retention Policy management](#rpManagement)
- [Shards management](#shardManagement)
# Versions <a name="version"></a>
There is avaible version for scala `2.11` and `2.12`. JDK 8 is required.
# Usage <a name="usage"></a>
## Connection <a name="connection"></a>
### Integration <a name="integration"></a>
Add to your dependencies list in `build.sbt`:
```
libraryDependencies += "com.github.fsanaulla" %% "chronicler" % "0.2"
```
### Imports <a name="import"></a>
```
// import executor

import scala.concurrent.ExecutionContext.Implicits.global

// or define your own implicit executor for specific needs in the scope

implicit val ex: ExecutionContext = _
```
### Create connection <a name="createConn"></a>
Creating simply `HTTP` connection based on `host` and default `port`
```
val influx = InfluxClientsFactory.createHttpClient("host") // default port 8086
```
or with `host` and custom `port`
```
val influx = InfluxClientsFactory.createHttpClient("host", 8087)
```
or with user auth info
```
val influx = InfluxClientsFactory.createHttpClient("host", 8087, Some("username"), Some("password"))
```
TO create `UDP` connection you need simply define host address and port:
```
val udpInflux = InfluxClientsFactory.createUdpClient("host", 8089)
```
## Helper tools <a name="helptools"></a>
### Time <a name="time"></a>
In many place you need to specify special influx time format, like in `duration` related fields. In this case you can simply write string based time like, `1h30m45s` by hand according to [Duration Time Format](https://docs.influxdata.com/influxdb/v1.3/query_language/spec/#durations).
Or use `InfluxDuration` object.
```
import com.fsanaulla.utils.InfluxDuration._

// Simply write
1.hours + 30.minutes + 45.seconds // that equal to 1h30m45s

// another existed extension
1.nanoseconds
1.microseconds
1.milliseconds
1.seconds
1.minutes
1.hours
1.days
1.weeks

// the same for Long
```
### Synchronize <a name="sync"></a>
To complete future you can use Extension object
```
import com.fsanaulla.utils.Synchronization._
import scala.concurrent.duration._

implicit val timeout = 1 second

val future: Future[T] = _
val completedFuture: T = future.sync
```
## Response handling <a name="resp"></a>
For now you need to understand how handle response from library api. Most of method that have querying functionality return `QueryResult`. What looks like:
```
case class QueryResult[T](code: Int,                    // response HTTP code
                          isSuccess: Boolean,           // success status 
                          queryResult: Seq[T] = Nil,    // quered data  
                          ex: Option[Throwable] = None) // optional exception 
```
So in your code you can handle responses like that:
```
db.read[T]("SELECT * FROM some_measurement") map {
      case QueryResult(_, _, queryResult, None) => queryResult // if no exсeption exist
      case _ => // handle error
}
```
Another non-query method like `setPassword`, `setUserPrivileges` return a `Result` object:
```
case class Result(code: Int,                    // HTTP response code
                  isSuccess: Boolean,           // success status
                  ex: Option[Throwable] = None) // optional exception
```
To handle it:
```
influx.setUserPassword("SomeUser", "newPassword") map {
        case Result(_, _, None) => // succesfully finished operation
        case _ => // handle error
}
```
## Database management <a name="dbManagement"></a>
Main [Database management](https://docs.influxdata.com/influxdb/v1.3/query_language/database_management/) operation:

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
## Read and Write operation <a name="readWrite"></a>
### Read operations <a name="read"></a>
There is several read method exist. The base one is:
```
db.readJs("SELEC * FROM measurement")
res0: Future[QueryResult[JsArray]] // where JsArray it's influx point representation
```
You can execute multiple query's in one request:
```
db.bulkReadJs(Seq("SELECT * FROM measurement", "SELECT * FROM measurement1"))
res0: Future[QueryResult[Seq[JsArray]]]
```
The next one it's typed method, for using it you need define your own `InfluxReader[T]` and add it implicitly to scope. There is example of one on that.
Simply mark it with `readable` annotation to generate implicits at compile time
```
@readaable
case class FakeEntity(firstName: String, lastName: String, age: Int)
```
And then just use it
```
db.read[FakeEntity]("SELECT * FROM measurement")
res0: Future[QueryResult[FakeEntity]]
```
## User management <a name="userManagement"></a>
Main [User Management](https://docs.influxdata.com/influxdb/v1.3/query_language/authentication_and_authorization/#user-management-commands) operations

Create non-admin user:
```
influx.createUser("UserName", "UserPassword")
res0: Future[Result]
```
Create admin user:
```
influx.createAdmin("AdminUser", "AdminPass")
res0: Future[Result]
```
Drop user:
```
influx.dropUser("UserName")
res0: Future[Result]
```
Set user password:
```
influx.setUserPassword("UserName", "UserPassword")
res0: Future[Result]
```
Set user privileges for some database:
```
import com.fsanaulla.utils.constants.Privileges._

influx.setPrivileges("SomeUser", "SomeDB", Privileges.READ)
res0: Future[Result]
```
Revoke privileges from user
```
import com.fsanaulla.utils.constants.Privileges._

influx.revokePrivileges("SomeUser", "SomeDB", Privileges.READ)
res0: Future[Result]
```
Make admin user from non-admin user:
```
influx.makeAdmin("NonAdminUser")
res0: Future[Result]
```
Demote admin user:
```
influx.disableAdmin("AdminUser")
res0: Future[Result]
```
Show users:
```
influx.showUsers()
res0: Future[Result]
```
Show user's privileges:
```
influx.showUserPrivileges()
res0: Future[Result]
```
## Continuously Query management <a name="CQManagement"></a>
Main [CQ management](https://docs.influxdata.com/influxdb/v1.3/query_language/continuous_queries/#continuous-query-management)
To create Continuously Query(further CQ). Where query params look's like `SELECT count("bees") AS "count_bees" INTO "aggregate_bees" FROM "farm" GROUP BY time(30m)`:
```
influx.createCQ("dbName", "cqName", "query")
res0: Future[Result]
```
Droping CQ:
```
influx.dropCQ("dbName", "cqName")
res0: Future[Result]
```
Show CQ's:
```
influx.showCQs()
res0: Future[QueryResult[ContinuousQueryInfo]]
```
Show database related CQ's:
```
influx.showCQ("dbName")
res0: Future[QueryResult[ContinuousQuery]]
```
There is no default update method for CQ, so `updateCQ` it's simulation using `dropCQ` and `createCQ` methods.
```
influx.updateCQ("dbName", "cqName", "query")
res0: Future[Result]
```
## Subscription management <a name="subsManagement"></a>
You can simply create `subscription`:
```
import com.fsanaulla.utils.constants.Destinations._

influx.createSubscription("subsName", "dbName", "rpName", Destinations.ALL, Seq("host1", "host2"))
res0: Future[Result]
```
To drop subscription:
```
influx.dropSubscription("subsName", "dbName", "rpName")
res0: Future[Result]
```
Show subscriptions:
```
influx.showSubscriptionsInfo()
res0: Future[QueryResult[SubscriptionInfo]]
```
To show database related subscriptions:
```
influx.showSubscriptions("dbName")
res0: Future[QueryResult[Subscription]]
```
There is no update method in subs api, so it's just simulation using drop and create:
```
import com.fsanaulla.utils.constants.Destinations._

influx.updateSubscription("subsName", "dbName", "rpName", Destinations.ALL, Seq("host1", "host2"))
res0: Future[Result]
```
## Retention Policy management <a name="rpManagement"></a>
Main [Retention Policy](https://docs.influxdata.com/influxdb/v1.3/query_language/database_management/)(further RP) operation
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
## Shard management <a name="shardManagement"></a>
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

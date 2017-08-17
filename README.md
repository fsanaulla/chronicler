# InfluxDB-Scala-client [![Build Status](https://travis-ci.org/fsanaulla/influxdb-scala-client.svg?branch=master)](https://travis-ci.org/fsanaulla/influxdb-scala-client) [![Coverage Status](https://coveralls.io/repos/github/fsanaulla/influxdb-scala-client/badge.svg?branch=master)](https://coveralls.io/github/fsanaulla/influxdb-scala-client?branch=master)

Asynchronous [Scala](https://www.scala-lang.org/) client library for [InfluxDB](https://www.influxdata.com/) based on [Akka HTTP](http://doc.akka.io/docs/akka-http/current/scala/http/).

# Usage
## Helper tools
#### Time
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
#### Synchronize
To complete future you can use Extension object
```
import com.fsanaulla.utils.Synchronization._
import scala.concurrent.duration._

implicit val timeout = 1 second

val future: Future[T] = _
val completedFuture: T = future.sync
```
## Connection
#### Imports
```
import com.fsanaulla.InfluxClient

// import executor

import scala.concurrent.ExecutionContext.Implicits.global

// or define your own implicit executor for specific needs in the scope

implicit val ex: ExecutionContext = _
```
#### Create connection
Creating simply connection based on `host` and default `port`
```
val influx = InfluxClient("host") //default port 8086
```
or with `host` and custom `port`
```
val influx = InfluxClient("host", 8087)
```
or with user auth info
```
val influx = InfluxClient("host", 8087, Some("username"), Some("password"))
```

## Data management
Main [Database management](https://docs.influxdata.com/influxdb/v1.3/query_language/database_management/) operation:
#### Create database

You can create db with such signatures
```
// With database name
influx.createDatabase("db")

// and with duration
influx.createDatabase("db", Some("2h))

// and with replication
influx.createDatabase("db", Some("2h"), Some(1))

// and with shard duration
influx.createDatabase("db", Some("2h"), Some(1), Some("2h"))

// and with associated retention policy name
influx.createDatabase("db", Some("2h"), Some(1), Some("2h"), Some("my_retention_policy"))
```

#### Select database
To choose needed database simply write:
```
val db: Database = influx.use("mydb")
```

#### Drop database
Specify database name
```
influx.dropDatabase("db_name")
```
#### Drop measurement
Specify measurement name
```
influx.dropMeasurement("measurement_name")
```
#### Drop shard
Specify shard id
```
influx.dropShard(2)
```
#### Show measurements
Specify database name
```
influx.showMeasurement("db_name").map(_.queryResult)
res0: Future[Seq[String]]
```
#### Show databases
```
influx.showDatabase().map(_.queryResult)
res0: Future[Seq[String]]
```
## Read and Write operation
#### Read operatione
There is several read method exist. The base one is:
```
db.readJs("SELEC * FROM measurement").map(_.queryResult)
res0: Future[Seq[JsArray]] // where JsArray it's influx point representation
```
The next one it's typed method, for using it you need define your own `InfluxReader[T]` and add it implicitly to scope. There is example of one on that:
```
case class FakeEntity(firstName: String, lastName: String, age: Int)

implicit object InfluxReaderFakeEntity extends InfluxReader[FakeEntity] {
    override def read(js: JsArray): FakeEntity = js.elements match {
      case Vector(_, JsNumber(age), JsString(name), JsString(lastName)) => FakeEntity(name, lastName, age.toInt)
      case _ => throw DeserializationException("Can't deserialize FakeEntity object")
    }
  }

```
And then just use it
```
import implicits.reader.location._

db.read[FakeEntity]("SELECT * FROM measurement").map(_.queryResult)
res0: Future[Seq[FakeEntity]]
```
You can execute multiple query's in one request:
```
db.bulkReadJs(Seq("SELECT * FROM measurement", "SELECT * FROM measurement1")).map(_.queryResult)
res0: Future[Seq[Seq[JsArray]]]
```
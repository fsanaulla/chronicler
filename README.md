# InfluxDB-Scala-client [![Build Status](https://travis-ci.org/fsanaulla/influxdb-scala-client.svg?branch=master)](https://travis-ci.org/fsanaulla/influxdb-scala-client) [![Coverage Status](https://coveralls.io/repos/github/fsanaulla/influxdb-scala-client/badge.svg?branch=master)](https://coveralls.io/github/fsanaulla/influxdb-scala-client?branch=master)

Asynchronous [Scala](https://www.scala-lang.org/) client library for [InfluxDB](https://www.influxdata.com/) based on [Akka HTTP](http://doc.akka.io/docs/akka-http/current/scala/http/). (IN PROGRESS)

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
#### Write operation
There is much more opportunities to store data.
First one save point in pure [Line Protocol Format](https://docs.influxdata.com/influxdb/v1.3/write_protocols/line_protocol_reference/)
```
// single
db.writeNative("cpu_load_short,host=server02,region=us-west value=0.55 1422568543702900257")
res0: Future[Result]

// bulk
db.bulkWriteNative(Seq("cpu_load_short,host=server02,region=us-west value=0.55 1422568543702900257", "cpu_load_short,host=server03,region=us-west value=0.56 1422568539002900257"))
res1: Future[Result]
```
`writePoint` add possibility to save `Point` into InfluxDB:
```
// single
val p1 = Point("testMeasurement")
            .addTag("name", "Jimbo")
            .addTag("surname", "Bimbo")
            .addField("age", 56)

db.writePoint(p1)
reso: Future[Result]

// bulk
db.bulkWritePoints(Seq(p1, ...))
res0: Future[Result]
```
Another one is typed method. That one can take any type that have implicit `InfluxWriter`object in the scope, that parse your object to [Line Protocol String](https://docs.influxdata.com/influxdb/v1.3/write_protocols/line_protocol_reference/). For example:
```
case class FakeEntity(firstName: String, lastName: String, age: Int)

implicit object InfluxWriterFakeEntity extends InfluxWriter[FakeEntity] {
  override def write(obj: FakeEntity): String = {
    s"firstName=${obj.firstName},lastName=${obj.lastName} age=${obj.age} $currentNanoTime"
  }
}
```
Then you can simply:
```
val fe = FakeEntity("Name", "Surname", 54)

// single
db.write[FakeEntity](fe)
res0: Future[Result]

// bulk
db.bulkWrite(Seq(fe, ...))
res0: Future[Result]
```

Another option is to write from file. File format example:
```
test1,host=server02 value=0.67
test1,host=server02,region=us-west value=0.55 1422568543702900257
test1,direction=in,host=server01,region=us-west value=2.0 1422568543702900257
```
every point must be on separate line in Line Protocol format.
```
db.writeFromFile("path/to/your/file")
res0: Future[Result]
```
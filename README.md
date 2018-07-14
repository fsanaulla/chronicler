<div align="center">

# Chronicler
Open-source [Scala](https://www.scala-lang.org/) client tool for [InfluxDB](https://www.influxdata.com/).

[![Build Status](https://travis-ci.org/fsanaulla/chronicler.svg?branch=master)](https://travis-ci.org/fsanaulla/chronicler)
[![Codecov](https://img.shields.io/codecov/c/github/fsanaulla/chronicler.svg)](https://codecov.io/gh/fsanaulla/chronicler)
[![Join the chat at https://gitter.im/chronicler/Lobby](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/chronicler-scala/Lobby/?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Latest version](https://index.scala-lang.org/fsanaulla/chronicler/chronicler-core-api/latest.svg?color=yellow)](https://index.scala-lang.org/com.github.fsanaulla/chronicler/chronicler-core-api_2.11)
[![License](http://img.shields.io/:license-Apache%202-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)
</div>

## Features
- Multiple backend
- Flexible API
- Code generation with macros
- High modularity
- Spark support (located [here](https://github.com/fsanaulla/chronicler-spark))
- Easy to customize

## Components
There are several components for use.

| Module | Description | 
| ------------- | ------------- |
| core | primitives for other modules | 
| macros | compile-time generating of `InfluxReader`, `InfluxWriter`, `InfluxFormatter`|
| akka-http | async HTTP client based on akka-http backend |
| async-client | async HTTP client based on async-client backend |
| url-http| sync HTTP client based on URLConnection backend |
| udp | UDP client|
| spark | Spark connector |

## Dependencies
**Core**:
- [enumeratum](https://github.com/lloydmeta/enumeratum) better scala enumerators
- [jawn](https://github.com/non/jawn) as a base for JSON operation

**Macros**:
- coreModel
- scala-reflect

**Akka-HTTP**:
- coreApi
- [akka-http](https://github.com/akka/akka-http)

**Async-HTTP**:
- coreApi
- [sttp](https://github.com/softwaremill/sttp) - async-client backend

**Url-HTTP**:
- coreApi
- [sttp](https://github.com/softwaremill/sttp) - url-conn backend

**UDP**
- coreModel

**Spark**
- Url-Http

## Installation
Add to your dependencies list in `build.sbt`:
```
// for Akka based client
libraryDependencies += "com.github.fsanaulla" %% "chronicler-akka-http" % <version>

// for Netty based client
libraryDependencies += "com.github.fsanaulla" %% "chronicler-async-http" % <version>

// for UrlHttp based client
libraryDependencies += "com.github.fsanaulla" %% "chronicler-url-http" % <version>

// for UDP protocol client
libraryDependencies += "com.github.fsanaulla" %% "chronicler-udp" % <version>

// macros extension
libraryDependencies += "com.github.fsanaulla" %% "chronicler-macros" % <version>
```

## Get started
Let's take a look on a simply example of usage. In this example we will use `async-http` client and `macros`.

Sbt file looks like:
```
lazy val chronicler: String = "latest"

libraryDependencies ++= Seq(
   "com.github.fsanaulla" %% "chronicler-async-http" % chronicler,
   "com.github.fsanaulla" %% "chronicler-macros"     % chronicler
)
```
Our code:
```
import com.github.fsanaulla.chronicler.async.{Influx, InfluxAsyncHttpClient}
import com.github.fsanaulla.macros.annotations.{field, tag, timestamp}
import com.github.fsanaulla.core.model.InfluxFormatter
import com.github.fsanaulla.chronicler.async.api.Measurement

import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

// let's define our model, and mark them with annotations for macro-code generation
case class Resume(
 @tag id: String,
 @tag candidateName: String,
 @tag candidateSurname: String,
 @field position: String,
 @field age: Option[Int],
 @field rate: Double,
 @timestamp created: Long)

// let's create serializers/deserializers 
implicit val fmt: InfluxFormatter[Resume] = Macros.format[Resume]

// setup credentials if exist
private val credentials: InfluxCredentials = InfluxCredentials("username", "password")

// influx details
final val host = "influx_host"
final val port = 8086

// establish connection to InfluxDB
val influx: AsyncIOClient = 
  Influx.io(host, port, Some(credentials)) // because we will make only IO action

val databaseName = "test_db"
val measurementName = "test_measurement"

// let's make it in type-safe approach
val measurement: Measurement[Resume] = 
  influx.measurement[Resume](databaseName, measurementName)
  
// let's write into measurement
val resume = Resume("dasdasfsadf",
                    "Jame",
                    "Lanni",
                    "Scala developer",
                    Some(25),
                    4.5,
                    System.currentTimeMillis() * 1000000)
  
// insert entity  
measurement.write(resume).onComplete {
  case Success(r) if r.isSuccess => println("Great new developer is coming!!")
  case _ => // handle failure
}

// retrieve entity
val result: Array[Resume] = measurement.read("SELECT * FROM $measurementName").onComplete {
  case Success(qr) if qr.isSuccess => qr.queryResult
  case _ => // handle failure
}

// close client
influx.close()
```
For more details see next section. The same example can be applied for other client. With small difference.

# Documentation
1. [Clients tutorial](docs/clients.md)
2. [Read operation](docs/read_operation_notes.md)
3. [Write operation](docs/write_operation_notes.md)
4. [Database management](docs/database_management.md)
5. [User management](docs/user_management.md)
6. [CQ management](docs/continuous_query-management.md)
7. [Subscription management](docs/subscription_management.md)
8. [RP management](docs/retention_policy_management.md)
9. [Shards management](docs/shard_management.md)
10. [Response handling](docs/response_handling.md)
11. [Macros](docs/macros.md)
12. [Utils](docs/utils.md)

# Inspirations
- [scala-influxdb-client](https://github.com/paulgoldbaum/scala-influxdb-client) by [Paul Goldbaum](https://github.com/paulgoldbaum)
- [influxdb-java](https://github.com/influxdata/influxdb-java) from [influxdata](https://github.com/influxdata)

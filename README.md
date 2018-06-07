<div align="center">

# Chronicler
Open-source [Scala](https://www.scala-lang.org/) client tool for [InfluxDB](https://www.influxdata.com/).

[![Build Status](https://travis-ci.org/fsanaulla/chronicler.svg?branch=master)](https://travis-ci.org/fsanaulla/chronicler)
[![Codecov](https://img.shields.io/codecov/c/github/fsanaulla/chronicler.svg)](https://codecov.io/gh/fsanaulla/chronicler)
[Download](https://img.shields.io/maven-central/v/com.github.fsanaulla/chronicler-core_2.11.svg)
[![License](http://img.shields.io/:license-Apache%202-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)
</div>

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

## Dependencies
**Core**:
- [enumeratum](https://github.com/lloydmeta/enumeratum) better scala enumerators
- [jawn](https://github.com/non/jawn) as a base for JSON operation

**Macros**:
- core
- scala-reflect

**Akka-HTTP**:
- core
- [akka-http](https://github.com/akka/akka-http)

**Async-HTTP**:
- core
- [sttp](https://github.com/softwaremill/sttp) - async-client backend

**Url-HTTP**:
- core
- [sttp](https://github.com/softwaremill/sttp) - url-conn backend

**UDP**
- core

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
lazy val chronicler: String = "0.2.4"

libraryDependencies ++= Seq(
   "com.github.fsanaulla" %% "chronicler-async-http" % chronicler,
   "com.github.fsanaulla" %% "chronicler-macros"     % chronicler
)
```
Our code:
```scala
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
val influx: InfluxAsyncHttpClient = 
  Influx.connect(host, port, Some(credentials))

val databaseName = "test_db"
val measurementName = "test_measurement"

// let's make it in type-safe approach
val measurement: Measurement[Resume] = 
  influx.measurement[Resume](databaseName, measurementName)
  
// let's write into measurement
val resume = 
  Resume("dasdasfsadf", "Jame", "Lanni", "Scala developer", Some(25), 4.5, 12312312L)
  
// insert entity  
measurement.write(resume).onComplete {
  case Success(r) if r.isSuccess => println("Great new developer is coming!!")
  case _ => // handle failure
}

// retrieve entity
val result: Array[Resume] = measurement.read("SELECT * FROM $measurement").onComplete {
  case Success(qr) if qr.isSuccess => qr.queryResult
  case _ => // handle failure
}

// close client
influx.close()
```
For more details see next section. The same example can be applied for other client. With small difference.

# Documentation
1. [Read operation](docs/read_operation_notes.md)
2. [Write operation](docs/write_operation_notes.md)
3. [Database management](docs/database_management.md)
4. [User management](docs/user_management.md)
5. [CQ management](docs/continuous_query-management.md)
6. [Subscription management](docs/subscription_management.md)
7. [RP management](docs/retention_policy_management.md)
8. [Shards management](docs/shard_management.md)
9. [Response handling](docs/response_handling.md)
10. [Macros](docs/macros.md)
11. [Utils](docs/utils.md)

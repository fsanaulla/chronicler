# InfluxDB-Scala-client [![Build Status](https://travis-ci.org/fsanaulla/influxdb-scala-client.svg?branch=master)](https://travis-ci.org/fsanaulla/influxdb-scala-client) [![Coverage Status](https://coveralls.io/repos/github/fsanaulla/influxdb-scala-client/badge.svg?branch=master)](https://coveralls.io/github/fsanaulla/influxdb-scala-client?branch=master)

Async/Sync [Scala](https://www.scala-lang.org/) client library for [InfluxDB](https://www.influxdata.com/).

# Usage

## Connection

Necessary imports
```
import com.fsanaulla.InfluxClient

// import executor

import scala.concurrent.ExecutionContext.Implicits.global

// or define own implicit executor for specific needs in the scope

implicit val ex: ExecutionContext = _
```

Creating simply connection based on `host` with default `port`
```
val influx = InfluxClient("host") //default port 8086
```
or with `host` and `port`
```
val influx = InfluxClient("host", 8087)
```
or with user auth info
```
val influx = InfluxClient("host", 8087, Some("username"), Some("password"))
```

## Database operations

### Select database

To choose needed database simply write:
```
val db: Database = influx.use("mydb")
```

### Writing data(impl in future)
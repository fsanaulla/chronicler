# Chronicler [![Build Status](https://travis-ci.org/fsanaulla/chronicler.svg?branch=master)](https://travis-ci.org/fsanaulla/chronicler) [![Coverage Status](https://coveralls.io/repos/github/fsanaulla/chronicler/badge.svg?branch=master)](https://coveralls.io/github/fsanaulla/chronicler?branch=master)

Chronicler - asynchronous [Scala](https://www.scala-lang.org/) client library for [InfluxDB](https://www.influxdata.com/).

# Table of content
- [Versions](#version)
- [Usage](#usage)
- [Connection](#connection)
    - [Integration](#integration)
    - [Imports](#import)
    - [Create connection](#createConn)
- [Helper Tools](docs/helper_tools.md)
- [Database management](docs/database_management.md)
- [Read operation](docs/read_operation_notes.md)
- [Write operation](docs/write_operation_notes.md)
- [User management](docs/user_management.md)
- [Continuously Query management](docs/continuous_query-management.md)
- [Subscription management](docs/subscription_management.md)
- [Retention Policy management](docs/retention_policy_management.md)
- [Shards management](docs/shard_management.md)
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
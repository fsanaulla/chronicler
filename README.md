<div align="center">

# Chronicler
Open-source [Scala](https://www.scala-lang.org/) client tool for [InfluxDB](https://www.influxdata.com/).

[![Build Status](https://travis-ci.org/fsanaulla/chronicler.svg?branch=master)](https://travis-ci.org/fsanaulla/chronicler)
[![Codecov](https://img.shields.io/codecov/c/github/fsanaulla/chronicler.svg)](https://codecov.io/gh/fsanaulla/chronicler)
[Download](https://img.shields.io/maven-central/v/com.github.fsanaulla/chronicler-core_2.11.svg)
[![License](http://img.shields.io/:license-Apache%202-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)
</div>

# Components
There are several components for use.

 | Module | Description | 
| ------------- | ------------- |
| core | primitives for other modules | 
| macros | compile-time generating of `InfluxReader`, `InfluxWriter`, `InfluxFormatter`|
| akka-http | async HTTP client based on akka-http backend |
| async-client | async HTTP client based on async-client backend |
| url-http| sync HTTP client based on URLConnection backend |
| udp | UDP client|

# Dependencies
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

# Installation
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

# Table of content
1. [Get started](docs/get_started.md)
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

<div align="center">

# Chronicler
[![Build Status](https://travis-ci.org/fsanaulla/chronicler.svg?branch=master)](https://travis-ci.org/fsanaulla/chronicler)
[![Codecov](https://img.shields.io/codecov/c/github/fsanaulla/chronicler.svg)](https://codecov.io/gh/fsanaulla/chronicler)
[![License](http://img.shields.io/:license-Apache%202-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)
</div>

# About project
Chronicler - asynchronous [Scala](https://www.scala-lang.org/) client library for [InfluxDB](https://www.influxdata.com/).
With several implementation, that allow you choose what you want. It support scala `2.11` and `2.12`.

| Project | Version |
| ------------- | ------------- |
| `chronicler-akka-http` | ![Download](https://img.shields.io/maven-central/v/com.github.fsanaulla/chronicler-akka-http_2.11.svg) |
| `chronicler-async-http` | ![Download](https://img.shields.io/maven-central/v/com.github.fsanaulla/chronicler-async-http_2.11.svg) |
| `chronicler-udp` | ![Download](https://img.shields.io/maven-central/v/com.github.fsanaulla/chronicler-udp_2.11.svg) |
| `chronicler-macros` | ![Download](https://img.shields.io/maven-central/v/com.github.fsanaulla/chronicler-macros_2.11.svg) |


# Installation
Add to your dependencies list in `build.sbt`:
```
// for Akka based client
libraryDependencies += "com.github.fsanaulla" %% "chronicler-akka-http" % <version>

// for Netty based client
libraryDependencies += "com.github.fsanaulla" %% "chronicler-async-http" % <version>

// for UDP protocol client
libraryDependencies += "com.github.fsanaulla" %% "chronicler-udp" % <version>

// macros extension
libraryDependencies += "com.github.fsanaulla" %% "chronicler-macros" % <version>
```
# Roadmap

| Task | Description | Status |
| ------------- | ------------- | ---------- |
| Netty based client | multiple backed type | Completed |
| Macro Formaters | Allow generating `InfluxReader[T]`, `InfluxWriter[T]` at compile time | Completed |
| Spark integration | Adding support for Spark -> InfluxDB pipeline | Not started |
| Type safe query DSL | Flexible method for query building | Not started |

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


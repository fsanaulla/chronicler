# Overview
Chronicler is a open-source library that provide toolchain for [InfluxDB](https://www.influxdata.com/) for [Scala programming language](https://www.scala-lang.org/).

[![Build Status](https://travis-ci.org/fsanaulla/chronicler.svg?branch=master)](https://travis-ci.org/fsanaulla/chronicler)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/19207668eaf6496485a66d2e2c7701c1)](https://www.codacy.com/app/fsanaulla/chronicler?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=fsanaulla/chronicler&amp;utm_campaign=Badge_Grade)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.fsanaulla/chronicler-core-shared_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.fsanaulla/chronicler-core-shared_2.11)
[![Join the chat at https://gitter.im/chronicler/Lobby](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/chronicler-scala/Lobby/?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

# Features
- Multiple backend(see [modules]($modules))
- Flexible API
- Code generation with macros
- High modularity
- Response streaming
- Data compression
- Spark support (located [here](https://github.com/fsanaulla/chronicler-spark))
- Kafka sink (in-progress)

# Modules
There are several modules. We can split them into three group:
- http
- udp
- utils

## Http
There are several backend-specific modules:
- akka, based on [akka-http](https://github.com/akka/akka-http)
- async-http-client, based on [this](https://github.com/AsyncHttpClient/async-http-client)
- java url-connection, based on standard java IO api

They in turn can be divided by functionality into: 
- IO operations, like `read`, `write`
- Management operation, like `createUser`, `revokeUser`

So final module list looks like:
- `chronicler-akka-io`
- `chronicler-akka-management`
- `chronicler-ahc-io`
- `chronicler-ahc-management`
- `chronicler-url-io`
- `chronicler-url-management`

## Udp
Writing using UDP protocol support located in `chronicler-udp`.

## Utils
Automatic derivation of `InfluxReader[_]` and `InfluxWriter[_]` located in `chronicler-macros`.

# Getting started
Chronicler is currently available for Scala 2.11, 2.12, 2.13.0. Add the following to your `build.sbt`:
```
libraryDependencies += "com.github.fsanaulla" %% <module-name> % <version>
``` 


# Documentation
1. [Modules](modules.md)
1. [Get Started](get_started.md)
1. [Clients tutorial](clients.md)
2. [Read operation](read_operation_notes.md)
3. [Write operation](write_operation_notes.md)
4. [Database management](database_management.md)
5. [User management](user_management.md)
6. [CQ management](continuous_query-management.md)
7. [Subscription management](subscription_management.md)
8. [RP management](retention_policy_management.md)
9. [Shards management](shard_management.md)
10. [Response handling](response_handling.md)
11. [Macros](macros.md)
12. [Utils](utils.md)
13. [Streaming](streaming.md)
14. [Data compression](gzipping.md)

# Inspirations
- [scala-influxdb-client](https://github.com/paulgoldbaum/scala-influxdb-client) by [Paul Goldbaum](https://github.com/paulgoldbaum)
- [influxdb-java](https://github.com/influxdata/influxdb-java) from [influxdata](https://github.com/influxdata)

<div align="center">

# Chronicler
[![Build Status](https://travis-ci.org/fsanaulla/chronicler.svg?branch=master)](https://travis-ci.org/fsanaulla/chronicler)
[![Codecov](https://img.shields.io/codecov/c/github/fsanaulla/chronicler.svg)](https://codecov.io/gh/fsanaulla/chronicler)
[Download](https://img.shields.io/maven-central/v/com.github.fsanaulla/chronicler-core_2.11.svg)
[![License](http://img.shields.io/:license-Apache%202-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)
</div>

# About project
Chronicler - open-source [Scala](https://www.scala-lang.org/) tool for [InfluxDB](https://www.influxdata.com/).

# Components
There are several components for use.

## Components
 - **core** module. Contain all necessary prmitives for other components.
 - client modules:
    - HTTP:
        - async clients:
            - **akka-http** - [akka](https://akka.io/) based client.
            - **async-http** - [netty](https://netty.io/) based client
        - sync clients
            - **url-http** - [HttpUrlConnection](https://docs.oracle.com/javase/8/docs/api/java/net/HttpURLConnection.html) based client.
    - UDP
        - **udp** - [datagram socket](https://docs.oracle.com/javase/8/docs/api/java/net/DatagramSocket.html) based client        
 - **macros** module. Provide compile-time macros for creating serializers and deserializers.       

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
# Roadmap

| Task | Description | Status |
| ------------- | ------------- | ---------- |
| Several client | multiple backed type | Completed |
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


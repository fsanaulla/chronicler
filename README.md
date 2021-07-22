<div align="center">

## Chronicler

Open-source [Scala](https://www.scala-lang.org/) toolchain for [InfluxDB](https://www.influxdata.com/).

[![Scala Testing Pipeline](https://github.com/fsanaulla/chronicler/actions/workflows/scala.yml/badge.svg)](https://github.com/fsanaulla/chronicler/actions/workflows/scala.yml)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.fsanaulla/chronicler-core-shared_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.fsanaulla/chronicler-core-shared_2.11)
[![Join the chat at https://gitter.im/chronicler/Lobby](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/chronicler-scala/Lobby/?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
</div>

## Features

- Multiple backend
- Flexible API
- Code generation with macros
- High modularity
- Response streaming
- Data compression (in-progress)
- Spark support (located [here](https://github.com/fsanaulla/chronicler-spark))
- Kafka sink (in-progress)

# Documentation

1. [Modules](docs/modules.md)
1. [Get Started](docs/get_started.md)
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
13. [Streaming](docs/streaming.md)
14. [Data compression](docs/gzipping.md)

# Inspirations

- [scala-influxdb-client](https://github.com/paulgoldbaum/scala-influxdb-client) by [Paul Goldbaum](https://github.com/paulgoldbaum)
- [influxdb-java](https://github.com/influxdata/influxdb-java) from [influxdata](https://github.com/influxdata)

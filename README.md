<div align="center">

# Chronicler
![CircleCI](https://circleci.com/gh/fsanaulla/chronicler.svg?style=shield&circle-token=3943b9e35ee6ec63d54741e57a2833a4609b9adc)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/7e195f786666462da89b22e27600fcc8)](https://app.codacy.com/app/fsanaulla/chronicler?utm_source=github.com&utm_medium=referral&utm_content=fsanaulla/chronicler&utm_campaign=badger)
</div>

# About project
Chronicler - asynchronous [Scala](https://www.scala-lang.org/) client library for [InfluxDB](https://www.influxdata.com/).
With several implementation, that allow you choose what you want. It support scala `2.11` and `2.12`.

| Project | Version |
| ------------- | ------------- |
| `chronicler-akka-http` | [![Latest version](https://index.scala-lang.org/fsanaulla/chronicler/chronicler-akka-http/latest.svg)](https://index.scala-lang.org/fsanaulla/chronicler/chronicler-akka-http) |
| `chronicler-async-http` | [![Latest version](https://index.scala-lang.org/fsanaulla/chronicler/chronicler-async-http/latest.svg)](https://index.scala-lang.org/fsanaulla/chronicler/chronicler-async-http) |
| `chronicler-udp` | [![Latest version](https://index.scala-lang.org/fsanaulla/chronicler/chronicler-udp/latest.svg)](https://index.scala-lang.org/fsanaulla/chronicler/chronicler-udp) |
| `chronicler-macros` | [![Latest version](https://index.scala-lang.org/fsanaulla/chronicler/chronicler-macros/latest.svg)](https://index.scala-lang.org/fsanaulla/chronicler/chronicler-macros) |


# Installation
Add to your dependencies list in `build.sbt`:
```
// for Akka based client
libraryDependencies += "com.github.fsanaulla" %% "chronicler-akka-http" % <version>

// for Netty based client
libraryDependencies += "com.github.fsanaulla" %% "chronicler-async-http" % <version>

// for UDP protocol client
libraryDependencies += "com.github.fsanaulla" %% "chronicler-udp" % <version>
```
# Roadmap

| Task | Description | Status |
| ------------- | ------------- | ---------- |
| Macro Formaters | Allow generating `InfluxReader[T]`, `InfluxWriter[T]` at compile time | Completed |
| Type safe query DSL | More flexible method for quering information | Not started |

# Table of content
- [Get started](docs/get_started.md)
- [Read operation](docs/read_operation_notes.md)
- [Write operation](docs/write_operation_notes.md)
- [Database management](docs/database_management.md)
- [User management](docs/user_management.md)
- [Continuously Query management](docs/continuous_query-management.md)
- [Subscription management](docs/subscription_management.md)
- [Retention Policy management](docs/retention_policy_management.md)
- [Shards management](docs/shard_management.md)
- [Helper Tools](docs/helper_tools.md)
- [Macros](docs/macros.md)

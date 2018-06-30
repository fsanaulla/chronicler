# Clients

At the moment there are 4 base clients connector for influx under chronicler project.
All HTTP client have the same api with different backend under the hood.

To configure it you should specify several supported params:

- influxdb host
- influxdb port
- influxdb optional credentials
- gzip support flag, it will automatically encode request body with [GZIP](https://en.wikipedia.org/wiki/Gzip) compression

It will looks like that:
```scala
val influx = Influx.connect(
  "172.12.200.43",
  8086,
  Some(InfluxCredentials("admin", "admin"))
  gzipped = true)
```
After that we will receive connected client with gzipped request

All client have additional params in their constructor, for more information take a look on connect function.

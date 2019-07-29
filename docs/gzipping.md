# Gzip

To enable gzip data compression, just pass flag in client constructor.
```scala
val influx: AhcIOClient = 
  InfluxIO(
    host,
    port,
    Some(credentials), 
    gzipping = true // enable gzipping
  )
```
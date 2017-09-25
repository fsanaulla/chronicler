# Set up connection
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
# Get started
First of all you need to create client for InfluxDB.
There are several options to create it:
## HTTP client
This library created to support same api for both clients, 
so all guideline can be applied to both of clients, Akka and Netty based.
```
// to create default client, that will connect to local instance of InfluxDB
// with HTTP interface on port 8086 and without any credentials, use:

val influx = InfluxDB.connect()

// to create client for non local InfluxDB instance, specify all necessary 
// parameters, as showed below:

val influx = InfluxDB.connect("some__host", 1234)

// add credentials to your client, first of all, define your credentials:

val creds = InfluxCredentials("admin", "admin")
val influx = InfluxDB.connect("some_host", 1234, Some(creds))

// you have possibility to pass your Execution context to InfluxDB client,
// it will catch it as implicitm parameter

import scala.concurrent.ExecutionContext.Implicits.global

val creds = InfluxCredentials("admin", "admin")
val influx = InfluxDB.connect("some_host", 1234, creds)
```
One difference between this two clients, that Akka based can receive `ActorSystem` as 4th parameter.
It can be useful, when you already define one `ActorSystem` in your project. For more information about clients constructor take a look on source code.

## UDP client
T create `UDP` connection you need simply define host address and port:
```
val udpInflux = InfluxClientsFactory.createUdpClient("host", 8089)
```
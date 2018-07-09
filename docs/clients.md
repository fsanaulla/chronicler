# Clients

## HTTP
At the moment there are 3 HTTP client for influx under chronicler project.
All client have the same api with different backend under the hood.

All of them have functionality delegation to smaller client:
- IO client
- Management client
- Full client

### *IO Client*

If you need to make IO related operations like read/write - choose IO client.
To create it call `io` method on Influx factory object like this:
```
val ioClient = Influx.io(...) // constructor parameters may wary depends on backend
```

### *Management Client*
If you need execute management operation like: create user/table/database/etc - choose Management client.
To create it call `management` method on Influx factory object like this:
```
val managementClient = Influx.management(...) // constructor parameters may wary depends on backend
```

### *Full Client*
It a combination of IO and Management clients.
To create it call `full` method on Influx factory object like this:
```
val fullClient = Influx.full(...) // constructor parameters may wary depends on backend
```

All client have additional params in their constructor, for more information take a look on connect function or ask question on [gitter](https://gitter.im/chronicler-scala/Lobby).


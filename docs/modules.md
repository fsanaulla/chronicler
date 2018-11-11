## Modules
There are several backend specific modules.

### Akka HTTP
```
// provide writing/quering api for Influx
libraryDependencies += "com.github.fsanaulla" %% "chronicler-akka-io" % <version>

// provide management api for Influx
libraryDependencies += "com.github.fsanaulla" %% "chronicler-akka-management" % <version>
```
### AHC(Async HTTP Client)
```
// provide writing/quering api for Influx
libraryDependencies += "com.github.fsanaulla" %% "chronicler-ahc-io" % <version>

// provide management api for Influx
libraryDependencies += "com.github.fsanaulla" %% "chronicler-ahc-management" % <version>
```
### HttpURLConnection
```
// provide writing/quering api for Influx
libraryDependencies += "com.github.fsanaulla" %% "chronicler-url-io" % <version>

// provide management api for Influx
libraryDependencies += "com.github.fsanaulla" %% "chronicler-url-management" % <version>
```
### UDP
```
// provide UDP client for Influx
libraryDependencies += "com.github.fsanaulla" %% "chronicler-udp" % <version>
```
### Macros
```
libraryDependencies += "com.github.fsanaulla" %% "chronicler-macros" % <version>
```
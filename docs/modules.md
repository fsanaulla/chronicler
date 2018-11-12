## Modules
There are several backend specific modules. All HTTP modules split into smaller jars.
First one provide `io` aka write/query api. And second one provide `management` api.

### Akka HTTP
```
libraryDependencies += "com.github.fsanaulla" %% "chronicler-akka-io" % <version>
libraryDependencies += "com.github.fsanaulla" %% "chronicler-akka-management" % <version>
```
### AHC(Async HTTP Client)
```
libraryDependencies += "com.github.fsanaulla" %% "chronicler-ahc-io" % <version>
libraryDependencies += "com.github.fsanaulla" %% "chronicler-ahc-management" % <version>
```
### HttpURLConnection
```
libraryDependencies += "com.github.fsanaulla" %% "chronicler-url-io" % <version>
libraryDependencies += "com.github.fsanaulla" %% "chronicler-url-management" % <version>
```
### UDP
```
libraryDependencies += "com.github.fsanaulla" %% "chronicler-udp" % <version>
```
### Macros
```
libraryDependencies += "com.github.fsanaulla" %% "chronicler-macros" % <version>
```
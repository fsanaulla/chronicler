## Modules
There are several backend specific modules. All HTTP modules split into smaller jars.
First one provide `io` aka write/query api. And second one provide `management` api.

### Akka based
In case when your program already contains actor system. This module is preferable solution.
```
libraryDependencies += "com.github.fsanaulla" %% "chronicler-akka-io" % <version>
libraryDependencies += "com.github.fsanaulla" %% "chronicler-akka-management" % <version>
```
### AHC(Async HTTP Client)
The most general solution for you program
```
libraryDependencies += "com.github.fsanaulla" %% "chronicler-ahc-io" % <version>
libraryDependencies += "com.github.fsanaulla" %% "chronicler-ahc-management" % <version>
```
### HttpURLConnection
Preferable module for sync code execution
```
libraryDependencies += "com.github.fsanaulla" %% "chronicler-url-io" % <version>
libraryDependencies += "com.github.fsanaulla" %% "chronicler-url-management" % <version>
```
### UDP
Connector for UDP communication. Used as a core for Spark module
```
libraryDependencies += "com.github.fsanaulla" %% "chronicler-udp" % <version>
```
### Macros
Helpful, well-optimized module to simplify you life in compile-time
```
libraryDependencies += "com.github.fsanaulla" %% "chronicler-macros" % <version>
```
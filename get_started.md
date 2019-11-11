## Get started
Let's take a look on a simply example of usage. In this example we will use `ahc` client and `macros`.

Sbt file looks like:
```sbt
lazy val chronicler: String = "latest"

libraryDependencies ++= Seq(
   "com.github.fsanaulla" %% "chronicler-ahc-io" % chronicler,
   "com.github.fsanaulla" %% "chronicler-macros" % chronicler
)
```
Our code:
```scala
import com.github.fsanaulla.chronicler.ahc.io.{AhcIOClient, InfluxIO}
import com.github.fsanaulla.chronicler.macros.auto._
import com.github.fsanaulla.chronicler.core.model.InfluxFormatter
import com.github.fsanaulla.chronicler.ahc.io.api.Measurement

import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

// let's define our model, and mark them with annotations for macro-code generation
final case class Resume(
 @tag id: String,
 @tag candidateName: Option[String],
 @tag candidateSurname: String,
 @field position: String,
 @field age: Int,
 @field rate: Double,
 @epoch @timestamp created: Long)

// setup credentials if exist
private val credentials: InfluxCredentials = InfluxCredentials("username", "password")

// influx details
final val host = "influx_host"
final val port = 8086

// establish connection to InfluxDB
// because we will make only IO action
val influx: AhcIOClient = 
  InfluxIO(
    host,
    port,
    Some(credentials), 
    gzipping = true // enable gzipping
  )

val databaseName = "test_db"
val measurementName = "test_measurement"

// let's make it in type-safe approach
val measurement: Measurement[Resume] = 
  influx.measurement[Resume](databaseName, measurementName)
  
// let's write into measurement
val resume = Resume("dasdasfsadf",
                    Some("Jame"),
                    "Lanni",
                    "Scala developer",
                    25,
                    4.5,
                    System.currentTimeMillis() * 1000000)
  
// insert entity  
measurement.write(resume).onComplete {
  case Right(respCode)  => ...
  case Left(err)        => ...
}

// retrieve entity
val result: Array[Resume] = measurement.read("SELECT * FROM $measurementName").onComplete {
  case Right(resumes) => ...
  case Left(err)      => ...
}

// close client
influx.close()
```
For more details see next section. The same example can be applied for other client. With small difference.
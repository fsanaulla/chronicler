# Helper tools <a name="helptools"></a>
## Time <a name="time"></a>
In many place you need to specify special influx time format, like in `duration` related fields. In this case you can simply write string based time like, `1h30m45s` by hand according to [Duration Time Format](https://docs.influxdata.com/influxdb/v1.3/query_language/spec/#durations).
Or use `InfluxDuration` object.
```
import com.fsanaulla.utils.InfluxDuration._

// Simply write
1.hours + 30.minutes + 45.seconds // that equal to 1h30m45s

// another existed extension
1.nanoseconds
1.microseconds
1.milliseconds
1.seconds
1.minutes
1.hours
1.days
1.weeks

// the same for Long
```
## Synchronize <a name="sync"></a>
To complete future you can use Extension object
```
import com.fsanaulla.utils.Synchronization._
import scala.concurrent.duration._

implicit val timeout = 1 second

val future: Future[T] = _
val completedFuture: T = future.sync
```
## Response handling <a name="resp"></a>
For now you need to understand how handle response from library api. Most of method that have querying functionality return `QueryResult`. What looks like:
```
case class QueryResult[T](code: Int,                    // response HTTP code
                          isSuccess: Boolean,           // success status 
                          queryResult: Seq[T] = Nil,    // quered data  
                          ex: Option[Throwable] = None) // optional exception 
```
So in your code you can handle responses like that:
```
db.read[T]("SELECT * FROM some_measurement") map {
      case QueryResult(_, _, queryResult, None) => queryResult // if no exсeption exist
      case _ => // handle error
}
```
Another non-query method like `setPassword`, `setUserPrivileges` return a `Result` object:
```
case class Result(code: Int,                    // HTTP response code
                  isSuccess: Boolean,           // success status
                  ex: Option[Throwable] = None) // optional exception
```
To handle it:
```
influx.setUserPassword("SomeUser", "newPassword") map {
        case Result(_, _, None) => // succesfully finished operation
        case _ => // handle error
}
```
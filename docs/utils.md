# Helper tools <a name="helptools"></a>

## Time <a name="time"></a>
In many places you require to specify influx time format, like in `duration` related fields. In this case you can write based time like, `1h30m45s` by hand according to [Duration Time Format](https://docs.influxdata.com/influxdb/v1.3/query_language/spec/#durations).
Or use `InfluxDuration` object.
```
import com.github.fsanaulla.core.utils.InfluxDuration._

// Simply write
1.hours + 30.minutes + 45.seconds // that equal to 1h30m45s: String

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
# Macros
This module it's optional extension to automatically generation of `InfluxReader[T]`, `InfluxWriter[T]`.

## Quick start
Add to your `build.sbt`:
```
libraryDependencies += "com.github.fsanaulla" %% "chronicler-macros" % <version>
```
Annotate your case class:
```
final case class Test(@escape @tag name: String, @field age: Int, @timestamp time: Long)
```
Generate your writer/reader manually:
```
implicit val wr: InfluxWriter[Test] = Influx.writer[Test]
implicit val rd: InfluxReader[Test] = Influx.reader[Test]
```
or 
```
import com.github.fsanaulla.chronicler.macros.auto._
```
# Glossary of terms
## Tag
All [tag](https://docs.influxdata.com/influxdb/v1.7/concepts/glossary/#tag)'s field must be marked with `@tag`. 

Supported types: 
- **`String`** 
- **`Option[String]`**

Can be combined with additional `@escape` annotation for escaping [special character](https://docs.influxdata.com/influxdb/v1.7/write_protocols/line_protocol_tutorial/#special-characters-and-keywords).

## Field
All [field](https://docs.influxdata.com/influxdb/v1.7/concepts/glossary/#field)'s must be market with `@field` annotation. It can't be optional.

Supported types: 
- **`Int`**
- **`Long`**
- **`Double`**
- **`Boolean`**
- **`String`**

## Timestamp
It has different behavior for `InfluxReader[_]` and `InfluxWriter[_]`.

For `InfluxReader[_]` there are four options:

- you expect to receive timestamp in [epoch](https://en.wikipedia.org/wiki/Unix_time) format, then use `@epoch` + `@timestamp`. 
Then the field type should be **`Long`**. For example when you specify precision in query. 
  
  **Remember**: InfluxDB use nano precision by default.
- you expect to receive timestamo in [utc](https://www.ietf.org/rfc/rfc3339.txt) formar, then use `@utc` + `@timestamp`.
Then field type  should be **`String`**.
- if you expect to receive time to time epoch time to time utc timestamp, then use only `@timestamp`. 
Supported field type: **`String`**, **`Long`**
- if you won't receive timestamp, then not use any annotations at all.

Additional annotations will speed up your `InfluxReader[_]`.

For `InfluxWriter[_]` there are two options:
- if you want to pass custom timestamp mark related field with `@timestamp`, supported type: **`Long`**.
- if you don't care about timestamp, not use annotation then. 

## Escape
Special character should be [escaped](https://docs.influxdata.com/influxdb/v1.7/write_protocols/line_protocol_tutorial/#special-characters).
If you want to enable escaping on the field mark it with `@escape` annotation.
Rules: 
- if applied on `@tag` field, it will automatically escape tag key and tag value.
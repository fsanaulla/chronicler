# Macros
This module it's optional extension to automatically generation of `InfluxReader[T]`, `InfluxWriter[T]`, `InfluxFormatter[T]`.

## Quick start
Add to your `build.sbt`:
```
libraryDependencies += "com.github.fsanaulla" %% "chronicler-macros" % <version>
```
Annotate your case class:
```
case class Test(@escape @tag name: String, @field age: Int, @timestamp time: Long)
```
Generate your writer/reader/formatter:
```
implicit val wr: InfluxWriter[Test] = Influx.writer[Test]
implicit val rd: InfluxWriter[Test] = Influx.reader[Test]
implicit val fmt: InfluxWriter[Test] = Influx.formatter[Test]
```
# Glossary of terms
## Tag
All [tag](https://docs.influxdata.com/influxdb/v1.7/concepts/glossary/#tag)'s field must be marked with `@tag`. 

Supported types: 
- **`String`** 
- **`Option[String]`**

## Field
All [field](https://docs.influxdata.com/influxdb/v1.7/concepts/glossary/#field)'s must be market with `@field` annotation. It can't be optional.

Supported types: 
- **`Int`**
- **`Long`**
- **`Double`**
- **`Boolean`**
- **`String`**

## Timestamp
You can specify which field will be used as a influx [timestamp](https://docs.influxdata.com/influxdb/v1.5/concepts/glossary/#timestamp) in process serialization/deserialization by marking with `@timestamp`.
It's optional field. 
If it's not specified, time will be generated on database level. Otherwise will be set up from entity.
There are three options, depending on `timestamp` field representation:

- `@timestamp` works with UTC/Epoch time representation. 
Supported type:
    - **`String`**
    - **`Long`**

- `@timestampUTC` works with UTC [RFC3339](https://www.ietf.org/rfc/rfc3339.txt). 
Supported type: 
    - **`String`**
- `@timestampEpoch` works with [Epoch](https://en.wikipedia.org/wiki/Unix_time) time. 
If you specify in your query `epoch` param. Supported type:
    - **`Long`**
    
    **Remember**: InfluxDB use nano precision by default.
    
## Escape
Special character should be [escaped](https://docs.influxdata.com/influxdb/v1.7/write_protocols/line_protocol_tutorial/#special-characters).
If you want to enable escaping on the field mark it with `@escape` annotation.
Rules: 
- if applied on `@tag` field, it will automatically escape tag key and tag value.
- if applied on `@field` field, it will automatically escape field key.

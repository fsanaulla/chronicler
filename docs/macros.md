# Macros
This module it's optional extension to simplify code generation, in case of `InfluxReader[T]`, `InfluxWriter[T]`, `InfluxFormatter[T]`.

## Installation
To use it, add to your `build.sbt`:
```
libraryDependencies += "com.github.fsanaulla" %% "chronicler-macros" % <version>
```
## Tag
All [tag](https://docs.influxdata.com/influxdb/v1.5/concepts/glossary/#tag)'s field must be marked with `@tag`. It's can be  used for optional fields.

Supported scala types: **`String`**, **`Option[String]`**

## Field
All [field](https://docs.influxdata.com/influxdb/v1.5/concepts/glossary/#field)'s must be market with `@field` annotation. 

Supported scala types: **`Int`**, **`Long`**, **`Double`**, **`Boolean`**, **`String`**.

## Timestamp
You can specify which field will be used as a influx [timestamp](https://docs.influxdata.com/influxdb/v1.5/concepts/glossary/#timestamp) in process serialization/deserialization by marking with appropriate annotations.
It's optional field. If it's not specified, time will be generated on database level. Otherwise will be set up from entity.
There are three options, depending on `timestamp` field representation:
1. `@timestamp` - works with UTC/Epoch time representation.
2. `@timestampUTC` - UTC [RFC3339](https://www.ietf.org/rfc/rfc3339.txt)
3. `@timestampEpoch` - [Epoch](https://en.wikipedia.org/wiki/Unix_time) time. If you specify in your query `epoch` param.

**Remember**: InfluxDB use nano precision by default.

Supported scala type: **Long**.

# Usage
Feel the power of Macros.
Let's start from reader example:
```
case class Entity(@tag name: String,
                  @field age: Int, 
                  @timestamp time: Long)

// that's all, after compilation, at this place will apper valid InfluxReader[T]
implicit val rd: InfluxReader[Entity] = Influx.reader[Entity]

// it's required for using type method, such
```
For writer it's little bit differ, because you need specify [tag](https://docs.influxdata.com/influxdb/v1.5/concepts/key_concepts/#tag-key) and [field](https://docs.influxdata.com/influxdb/v1.5/concepts/key_concepts/#field-value). It can be done by simply annotating:
```
case class Entity(@tag name: String, @field age: Int)

// that's all, after compilation, at this place will apper valid InfluxWriter[T]
implicit val wr: InfluxWriter[Entity] = Influx.writer[Entity]

// it's required for using type method, such
meas.write[Entity](Entity("Martin", 54)
```
You can add both of them in the scope by using:
```
implicit val fmt: InfluxFormatter[Entity] = Influx.format[Entity]

meas.write[Entity](Entity("Martin", 54))
db.read[Entity]("SELECT * FROM some_meas")
```

In short it's look like:
1. Mark tags(`@tag`) and fields(`@field`), and optional (`@timestamp`, `@timestampUTC`, `@timestampEpoch`).
2. Generate reader/writer/formatter.

# Macros
This module it's optional extension to simplify code generation, in case of `InfluxReader[T]`, `InfluxWriter[T]`, `InfluxFormatter[T]`.

## Installation
To use it, add to your `build.sbt`:
```
libraryDependencies += "com.github.fsanaulla" %% "chronicler-macros" % <version>
```
## Tag
All [tag](https://docs.influxdata.com/influxdb/v1.5/concepts/glossary/#tag)'s field must be marked with `@tag`. It's can be  used for optional fields.

Supported types: **`String`**, **`Option[String]`**

## Field
All [field](https://docs.influxdata.com/influxdb/v1.5/concepts/glossary/#field)'s must be market with `@field` annotation. 

Supported types: **`Int`**, **`Long`**, **`Double`**, **`Boolean`**, **`String`**.

## Timestamp
You can specify which field will be used as a influx [timestamp](https://docs.influxdata.com/influxdb/v1.5/concepts/glossary/#timestamp) in process serialization/deserialization by marking with `@timestamp` annotation.
It's optional field. If it's not specified, time will be generated on database level. Otherwise will be setted from entity. 

**Remember**: InfluxDB use nano precision.

Supported type: **Long**.

# Usage
Feel the power of Macros.
Let's start from reader example:
```scala
case class Entity(@tag name: String,
                  @field age: Int, 
                  @timestamp time: Long)

// that's all, after compilation, at this place will apper valid InfluxReader[T]
implicit val rd: InfluxReader[Entity] = Macros.reader[Entity]

// it's required for using type method, such
```
For writer it's little bit differ, because you need specify [tag](https://docs.influxdata.com/influxdb/v1.5/concepts/key_concepts/#tag-key) and [field](https://docs.influxdata.com/influxdb/v1.5/concepts/key_concepts/#field-value). It can be done by simply annotating:
```scala
case class Entity(@tag name: String, @field age: Int)

// that's all, after compilation, at this place will apper valid InfluxWriter[T]
implicit val wr: InfluxWriter[Entity] = Macros.writer[Entity]

// it's required for using type method, such
meas.write[Entity](Entity("Martin", 54)
```
You can add both of them in the scope by using:
```scala
implicit val fmt: InfluxFormatter[Entity] = Macros.format[Entity]

meas.write[Entity](Entity("Martin", 54)
db.read[Entity]("SELECT * FROM some_meas")
```

In short it's look like:
1. Mark tags(`@tag`) and fields(`@field`), and optional (`@timestamp`).
2. Generate reader/writer/formatter.

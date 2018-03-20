# Macros
This module it's optional extension to simplify code generation, in case of `InfluxReader[T]`, `InfluxWriter[T]`, `InfluxFormatter[T]`.

## Installation
To use it, add to your `build.sbt`:
```
libraryDependencies += "com.github.fsanaulla" %% "chronicler-macros" % <version>
```
## Usage
Feel the power of Macros.
Let's start from reader example:
```
case class Entity(name: String, age: Int)

// that's all, after compilation, at this place will apper valid InfluxReader[T]
implicit val rd: InfluxReader[Entity] = Macros.reader[Entity]

// it's required for using type method, such
```
For writer it's little bit differ, because you need specify [tag](https://docs.influxdata.com/influxdb/v1.5/concepts/key_concepts/#tag-key) and [field](https://docs.influxdata.com/influxdb/v1.5/concepts/key_concepts/#field-value). It can be done by simply annotating:
```
case class Entity(@tag name: String, @field age: Int)

// that's all, after compilation, at this place will apper valid InfluxWriter[T]
implicit val wr: InfluxWriter[Entity] = Macros.writer[Entity]

// it's required for using type method, such
meas.write[Entity](Entity("Martin", 54)
```
You can add both of them in the scope by using:
```
implicit val fmt = Macros.format[Entity]

meas.write[Entity](Entity("Martin", 54)
db.read[Entity]("SELECT * FROM some_meas")
```

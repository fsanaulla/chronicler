package com.github.fsanaulla.core.model

import jawn.ast.JArray

import scala.annotation.implicitNotFound

/**
  * Created by fayaz on 27.06.17.
  */
@implicitNotFound(
  "No InfluxFormatter found for type ${T}. Try to implement an implicit Format for this type."
)
trait InfluxFormatter[T] extends InfluxReader[T] with InfluxWriter[T]

/**
  * Return string must be in following format
  * <measurement>,[<tag-key>=<tag-value>...] [<field-key>=<field-value>,<field2-key>=<field2-value>...] [unix-nano-timestamp]
  * Look on official documentation [https://docs.influxdata.com/influxdb/v1.2/write_protocols/line_protocol_reference/]
  */
@implicitNotFound(
  "No InfluxWriter found for type ${T}. Try to implement an implicit Writable for this type."
)
trait InfluxWriter[T] {
  def write(obj: T): String
}

/**
  * When trying deserialize JSON from influx, don't forget that influx sort field in db alphabetically.
  */
@implicitNotFound(
  "No InfluxReader found for type ${T}. Try to implement an implicit Readable for this type."
)
trait InfluxReader[T] {
  def read(js: JArray): T
}

package com.github.fsanaulla.chronicler.core.model

import java.io.{Serializable => JSerializable}

import scala.annotation.implicitNotFound


/**
  * Return string must be in following format
  * <measurement>,[<tag-key>=<tag-value>...] [<field-key>=<field-value>,<field2-key>=<field2-value>...] [unix-nano-timestamp]
  * Look on official documentation [https://docs.influxdata.com/influxdb/v1.2/write_protocols/line_protocol_reference/]
  */
@implicitNotFound(
  "No InfluxWriter found for type ${T}. Try to implement an implicit InfluxWriter for this type."
)
trait InfluxWriter[T] extends JSerializable {
  def write(obj: T): String
}

package com.fsanaulla.model

import scala.annotation.implicitNotFound

/**
  * Created by fayaz on 27.06.17.
  */

@implicitNotFound(
  "No Format found for type ${T}. Try to implement an implicit Format for this type."
)
trait Format[T] extends Readable[T] with Writable[T]

/**
  * Return string must be in following format
  * <measurement>,[<tag-key>=<tag-value>...] [<field-key>=<field-value>,<field2-key>=<field2-value>...] [unix-nano-timestamp]
  * Look on official documentation [https://docs.influxdata.com/influxdb/v1.2/write_protocols/line_protocol_reference/]
  */
@implicitNotFound(
  "No Writable found for type ${T}. Try to implement an implicit Writable for this type."
)
trait Writable[T] {
  def write(obj: T): String
}

@implicitNotFound(
  "No Readable found for type ${T}. Try to implement an implicit Readable for this type."
)
trait Readable[T] {
  def read(str: String): T
}

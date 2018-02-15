package com.github.fsanaulla.macros

import com.github.fsanaulla.core.model.{InfluxFormatter, InfluxReader, InfluxWriter}

import scala.language.experimental.macros

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 13.02.18
  */
object InfluxFormatter {

  /**
    * Generate InfluxWriter for type ${A}
    */
  def writer[A]: InfluxWriter[A] = macro InfluxFormatterImpl.writer_impl[A]

  /**
    * Generate InfluxReader for type ${A}
    */
  def reader[A]: InfluxReader[A] = ???

  /**
    * Generate InfluxFormatter for type ${A}
    */
  def format[A]: InfluxFormatter[A] = ???
}

package com.github.fsanaulla.macros

import com.github.fsanaulla.core.model.{InfluxFormatter, InfluxReader, InfluxWriter}

import scala.language.experimental.macros

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 13.02.18
  */
object Macros {

  /**
    * Generate InfluxWriter for type ${A}
    */
  def writer[A]: InfluxWriter[A] = macro MacrosImpl.writer_impl[A]

  /**
    * Generate InfluxReader for type ${A}
    */
  def reader[A]: InfluxReader[A] = macro MacrosImpl.reader_impl[A]

  /**
    * Generate InfluxFormatter for type ${A}
    */
  def format[A]: InfluxFormatter[A] = macro MacrosImpl.format_impl[A]
}

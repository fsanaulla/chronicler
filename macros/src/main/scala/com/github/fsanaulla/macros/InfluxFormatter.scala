package com.github.fsanaulla.macros

import com.github.fsanaulla.core.model.InfluxWriter

import scala.language.experimental.macros

object InfluxFormatter {
  /**
    * Generate InfluxWriter for type ${A}
    */
  def writer[A]: InfluxWriter[A] = macro InfluxFormatterImpl.writer_impl[A]
}

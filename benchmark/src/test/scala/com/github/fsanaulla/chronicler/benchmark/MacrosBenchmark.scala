package com.github.fsanaulla.chronicler.benchmark

import java.util.concurrent.TimeUnit

import com.github.fsanaulla.chronicler.benchmark.MacrosBenchmark.{CustomWriter, MacroWriter, Test}
import com.github.fsanaulla.chronicler.core.model.InfluxWriter
import com.github.fsanaulla.chronicler.macros.Influx
import com.github.fsanaulla.chronicler.macros.annotations.{field, tag, timestampEpoch}
import org.openjdk.jmh.annotations._

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MILLISECONDS)
class MacrosBenchmark {

  @Benchmark
  def averageMacroWriteTime(state: MacroWriter): Unit =
    state.writer.write(Test("a", Some("b"), 5, 150L))

  @Benchmark
  def averageWriteTime(state: CustomWriter): Unit =
    state.writer.write(Test("a", Some("b"), 5, 150L))
}

object MacrosBenchmark {
  final case class Test(@tag name: String,
                        @tag surname: Option[String],
                        @field age: Int,
                        @timestampEpoch time: Long)

  @State(Scope.Benchmark)
  class MacroWriter {
    var writer: InfluxWriter[Test] = _

    @Setup
    def up(): Unit =
      writer = Influx.writer[Test]

    @TearDown
    def close(): Unit = {}
  }

  @State(Scope.Benchmark)
  class CustomWriter {
    var writer: InfluxWriter[Test] = _

    @Setup
    def up(): Unit =
      writer = (obj: Test) => {
        val sb = new StringBuilder
        sb
          .append(obj.name)

        if (obj.surname.isDefined) {
          sb
            .append(",")
            .append(obj.surname.get)
        } else sb.append(" ")

        sb
          .append(obj.age)
          .append(" ")
          .append(obj.time)

        sb.toString()
      }

    @TearDown
    def close(): Unit = {}
  }
}

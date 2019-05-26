package com.github.fsanaulla.chronicler.benchmark

import java.util.concurrent.TimeUnit

import com.github.fsanaulla.chronicler.benchmark.InfluxReaderBenchmark._
import com.github.fsanaulla.chronicler.core.model.InfluxReader
import com.github.fsanaulla.chronicler.macros.annotations.reader.{epoch, utc}
import com.github.fsanaulla.chronicler.macros.annotations.{field, tag, timestamp}
import com.github.fsanaulla.chronicler.macros.auto._
import jawn.ast._
import org.openjdk.jmh.annotations._

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MILLISECONDS)
class InfluxReaderBenchmark {

  // safe
  @Benchmark
  def averageEpochTimestamp(state: EpochReader): Unit =
    state.reader.read(JArray(Array(LongNum(1438715114318570484L), JNum(4), JString("Fz"), JNull)))

  @Benchmark
  def averageUtcTimestamp(state: UtcReader): Unit =
    state.reader.read(JArray(Array(JString("2015-08-04T19:05:14.318570484Z"), JNum(4), JString("Fz"), JNull)))

  @Benchmark
  def averageGeneralEpochTimestamp(state: GeneralEpochReader): Unit =
    state.reader.read(JArray(Array(LongNum(1438715114318570484L), JNum(4), JString("Fz"), JNull)))

  @Benchmark
  def averageGeneralUtcTimestamp(state: GeneralUtcReader): Unit =
    state.reader.read(JArray(Array(JString("2015-08-04T19:05:14.318570484Z"), JNum(4), JString("Fz"), JNull)))

  // unsafe
  @Benchmark
  def averageEpochTimestampUnsafe(state: EpochReader): Unit =
    state.reader.readUnsafe(JArray(Array(LongNum(1438715114318570484L), JNum(4), JString("Fz"), JNull)))

  @Benchmark
  def averageUtcTimestampUnsafe(state: UtcReader): Unit =
    state.reader.readUnsafe(JArray(Array(JString("2015-08-04T19:05:14.318570484Z"), JNum(4), JString("Fz"), JNull)))

  @Benchmark
  def averageGeneralEpochTimestampUnsafe(state: GeneralEpochReader): Unit =
    state.reader.readUnsafe(JArray(Array(LongNum(1438715114318570484L), JNum(4), JString("Fz"), JNull)))

  @Benchmark
  def averageGeneralUtcTimestampUnsafe(state: GeneralUtcReader): Unit =
    state.reader.readUnsafe(JArray(Array(JString("2015-08-04T19:05:14.318570484Z"), JNum(4), JString("Fz"), JNull)))

}

object InfluxReaderBenchmark {
  final case class Epoch(@tag name: String,
                         @tag surname: Option[String],
                         @field age: Int,
                         @epoch @timestamp time: Long)

  final case class Utc(@tag name: String,
                       @tag surname: Option[String],
                       @field age: Int,
                       @utc @timestamp time: String)

  final case class GeneralEpoch(@tag name: String,
                                @tag surname: Option[String],
                                @field age: Int,
                                @timestamp time: Long)

  final case class GeneralUtc(@tag name: String,
                                @tag surname: Option[String],
                                @field age: Int,
                                @timestamp time: Long)


  @State(Scope.Benchmark)
  class EpochReader {
    var reader: InfluxReader[Epoch] = _
    @Setup
    def up(): Unit = reader = InfluxReader[Epoch]
    @TearDown
    def close(): Unit = {}
  }

  @State(Scope.Benchmark)
  class UtcReader {
    var reader: InfluxReader[Utc] = _
    @Setup
    def up(): Unit = reader = InfluxReader[Utc]
    @TearDown
    def close(): Unit = {}
  }

  @State(Scope.Benchmark)
  class GeneralEpochReader {
    var reader: InfluxReader[GeneralEpoch] = _
    @Setup
    def up(): Unit = reader = InfluxReader[GeneralEpoch]
    @TearDown
    def close(): Unit = {}
  }

  @State(Scope.Benchmark)
  class GeneralUtcReader {
    var reader: InfluxReader[GeneralUtc] = _
    @Setup
    def up(): Unit = reader = InfluxReader[GeneralUtc]
    @TearDown
    def close(): Unit = {}
  }
}

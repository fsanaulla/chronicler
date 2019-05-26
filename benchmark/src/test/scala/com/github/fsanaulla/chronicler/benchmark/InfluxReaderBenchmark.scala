package com.github.fsanaulla.chronicler.benchmark

import java.util.concurrent.TimeUnit

import com.github.fsanaulla.chronicler.benchmark.InfluxReaderBenchmark._
import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.model.InfluxReader
import com.github.fsanaulla.chronicler.macros.annotations.reader.{epoch, utc}
import com.github.fsanaulla.chronicler.macros.annotations.{field, tag, timestamp}
import com.github.fsanaulla.chronicler.macros.auto._
import jawn.ast._
import org.openjdk.jmh.annotations._

//[info] InfluxReaderBenchmark.averageCustomEpochTimestamp         avgt    5    18.809 ±   0.179  ns/op
//[info] InfluxReaderBenchmark.averageCustomEpochTimestampUnsafe   avgt    5    12.287 ±   1.172  ns/op
//[info] InfluxReaderBenchmark.averageEpochTimestamp               avgt    5   142.302 ±  83.900  ns/op
//[info] InfluxReaderBenchmark.averageEpochTimestampUnsafe         avgt    5   123.555 ±  14.805  ns/op
//[info] InfluxReaderBenchmark.averageGeneralEpochTimestamp        avgt    5   127.030 ±  12.423  ns/op
//[info] InfluxReaderBenchmark.averageGeneralEpochTimestampUnsafe  avgt    5   145.111 ±  56.139  ns/op
//[info] InfluxReaderBenchmark.averageGeneralUtcTimestamp          avgt    5  1014.081 ± 128.966  ns/op
//[info] InfluxReaderBenchmark.averageGeneralUtcTimestampUnsafe    avgt    5   994.313 ± 143.860  ns/op
//[info] InfluxReaderBenchmark.averageUtcTimestamp                 avgt    5   117.948 ±   1.048  ns/op
//[info] InfluxReaderBenchmark.averageUtcTimestampUnsafe           avgt    5   115.961 ±   1.142  ns/op
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.NANOSECONDS)
class InfluxReaderBenchmark {

  // custom
  @Benchmark
  def averageCustomEpochTimestamp(state: CustomEpochReader): Unit =
    state.reader.read(JArray(Array(LongNum(1438715114318570484L), JNum(4), JString("Fz"), JNull)))

  @Benchmark
  def averageCustomEpochTimestampUnsafe(state: CustomEpochReader): Unit =
    state.reader.readUnsafe(JArray(Array(LongNum(1438715114318570484L), JNum(4), JString("Fz"), JNull)))

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

  @State(Scope.Benchmark)
  class CustomEpochReader {
    var reader: InfluxReader[Epoch] = _
    @Setup
    def up(): Unit = reader = new InfluxReader[Epoch] {
      override def read(js: JArray): ErrorOr[Epoch] = {
        val arr = js.vs
        Right(
          Epoch(
            arr(2).asString,
            arr(3).getString,
            arr(1).asInt,
            arr(0).asLong
          )
        )
      }
      override def readUnsafe(js: JArray): Epoch = {
        val arr = js.vs
        Epoch(
          arr(2).asString,
          arr(3).getString,
          arr(1).asInt,
          arr(0).asLong
        )
      }
    }
    @TearDown
    def close(): Unit = {}
  }
}

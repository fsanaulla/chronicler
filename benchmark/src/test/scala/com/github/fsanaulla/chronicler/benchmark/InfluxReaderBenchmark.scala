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

import scala.util.Try

//[info] Benchmark                                                 Mode  Cnt    Score     Error  Units
//[info] InfluxReaderBenchmark.averageCustomEpochTimestamp         avgt    5   17.591 ±   0.777  ns/op
//[info] InfluxReaderBenchmark.averageCustomEpochTimestampUnsafe   avgt    5   12.113 ±   2.320  ns/op
//[info] InfluxReaderBenchmark.averageEpochTimestamp               avgt    5  130.291 ±  36.857  ns/op
//[info] InfluxReaderBenchmark.averageEpochTimestampUnsafe         avgt    5  121.529 ±  11.673  ns/op
//[info] InfluxReaderBenchmark.averageGeneralEpochTimestamp        avgt    5  127.058 ±   7.573  ns/op
//[info] InfluxReaderBenchmark.averageGeneralEpochTimestampUnsafe  avgt    5  124.862 ±   7.524  ns/op
//[info] InfluxReaderBenchmark.averageGeneralUtcTimestamp          avgt    5  980.548 ± 128.744  ns/op
//[info] InfluxReaderBenchmark.averageGeneralUtcTimestampUnsafe    avgt    5  972.418 ± 169.507  ns/op
//[info] InfluxReaderBenchmark.averageNewEpochTimestamp            avgt    5   16.830 ±   0.769  ns/op
//[info] InfluxReaderBenchmark.averageNewEpochTimestampUnsafe      avgt    5   12.286 ±   1.478  ns/op
//[info] InfluxReaderBenchmark.averageUtcTimestamp                 avgt    5  114.618 ±   1.397  ns/op
//[info] InfluxReaderBenchmark.averageUtcTimestampUnsafe           avgt    5  160.640 ± 160.653  ns/op
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.NANOSECONDS)
class InfluxReaderBenchmark {

  // new
  @Benchmark
  def averageNewEpochTimestamp(state: NewEpochReader): Unit =
    state.reader.read(JArray(Array(LongNum(1438715114318570484L), JNum(4), JString("Fz"), JNull)))

  @Benchmark
  def averageNewEpochTimestampUnsafe(state: NewEpochReader): Unit =
    state.reader.readUnsafe(JArray(Array(LongNum(1438715114318570484L), JNum(4), JString("Fz"), JNull)))

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
        Try(
          Epoch(
            arr(2).asString,
            arr(3).getString,
            arr(1).asInt,
            arr(0).asLong
          )
        ).toEither
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

  @State(Scope.Benchmark)
  class NewEpochReader {
    var reader: InfluxReader[Epoch] = _
    @Setup
    def up(): Unit = reader = InfluxReader[Epoch]
    @TearDown
    def close(): Unit = {}
  }
}

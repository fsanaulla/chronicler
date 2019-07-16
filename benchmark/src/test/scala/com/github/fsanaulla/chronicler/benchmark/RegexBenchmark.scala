package com.github.fsanaulla.chronicler.benchmark

import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

import com.github.fsanaulla.chronicler.benchmark.RegexBenchmark.CompiledPattern
import com.github.fsanaulla.chronicler.core.regex
import org.openjdk.jmh.annotations._

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MILLISECONDS)
class RegexBenchmark {

  // 10x time faster
  @Benchmark
  def compiledPattern(state: CompiledPattern): Unit =
    state.pattern.matcher("My=, Name").replaceAll("\\\\$1")

  @Benchmark
  def uncompiledPattern(): Unit =
    "My=, Name".replaceAll("([ ,=])", "\\\\$1")
}

object RegexBenchmark {
  @State(Scope.Benchmark)
  class CompiledPattern {
    var pattern: Pattern = _
    @Setup
    def up(): Unit = pattern = regex.tagPattern
    @TearDown
    def close(): Unit = {}
  }
}

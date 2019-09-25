package com.github.fsanaulla.chronicler.benchmark

import java.util.concurrent.TimeUnit

import com.github.fsanaulla.chronicler.benchmark.JsonHandlerBenchmark._
import com.github.fsanaulla.chronicler.core.jawn._
import org.openjdk.jmh.annotations._
import org.typelevel.jawn.ast.{JArray, JObject, JParser, JValue}

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.NANOSECONDS)
class JsonHandlerBenchmark {

  @Benchmark
  def queryResultBench(state: QueryResult): Unit = {
    queryResult(state.json)
  }

  @Benchmark
  def groupedResultBench(state: GroupedResult): Unit =
    groupedResult(state.json)

  @Benchmark
  def bulkResultBench(state: BulkQuery): Unit =
    bulkResult(state.json)

  @Benchmark
  def groupedSystemInfoJsBench(state: GroupedSystem): Unit =
    groupedSystemInfoJs(state.json)
}

object JsonHandlerBenchmark {

  @State(Scope.Benchmark)
  class QueryResult {
    var json: JValue = _

    @Setup
    def up(): Unit = {
      json = JParser.parseUnsafe("""{
                                   |"results": [
                                   |        {
                                   |            "statement_id": 0,
                                   |            "series": [
                                   |                {
                                   |                    "name": "cpu_load_short",
                                   |                    "columns": [
                                   |                        "time",
                                   |                        "value"
                                   |                    ],
                                   |                    "values": [
                                   |                        [
                                   |                            "2015-01-29T21:55:43.702900257Z",
                                   |                            2
                                   |                        ],
                                   |                        [
                                   |                            "2015-01-29T21:55:43.702900257Z",
                                   |                            0.55
                                   |                        ],
                                   |                        [
                                   |                            "2015-06-11T20:46:02Z",
                                   |                            0.64
                                   |                        ]
                                   |                    ]
                                   |                }
                                   |            ]
                                   |        }
                                   |    ]
                                   |}""".stripMargin)
    }

    @TearDown
    def close(): Unit = {}
  }

  @State(Scope.Benchmark)
  class GroupedResult {
    var json: JValue = _

    @Setup
    def up(): Unit = {
      json = JParser.parseUnsafe("""
                                   |{
                                   |   "results": [
                                   |     {
                                   |         "statement_id": 0,
                                   |         "series": [
                                   |           {
                                   |             "name": "cpu_load_short",
                                   |             "tags": {
                                   |               "host": "server01",
                                   |               "region": "us-west"
                                   |             },
                                   |             "columns": [
                                   |               "time",
                                   |               "mean"
                                   |             ],
                                   |             "values": [
                                   |               [
                                   |                 "1970-01-01T00:00:00Z",
                                   |                 0.69
                                   |               ]
                                   |             ]
                                   |           },
                                   |           {
                                   |             "name": "cpu_load_short",
                                   |             "tags": {
                                   |               "host": "server02",
                                   |               "region": "us-west"
                                   |             },
                                   |             "columns": [
                                   |               "time",
                                   |               "mean"
                                   |             ],
                                   |             "values": [
                                   |               [
                                   |                 "1970-01-01T00:00:00Z",
                                   |                 0.73
                                   |               ]
                                   |             ]
                                   |           }
                                   |         ]
                                   |     }
                                   |   ]
                                   |}""".stripMargin)
    }

    @TearDown
    def close(): Unit = {}
  }

  @State(Scope.Benchmark)
  class BulkQuery {
    var json: JValue = _

    @Setup
    def up(): Unit = {
      json = JParser.parseUnsafe("""{
                                   |    "results": [
                                   |        {
                                   |            "statement_id": 0,
                                   |            "series": [
                                   |                {
                                   |                    "name": "cpu_load_short",
                                   |                    "columns": [
                                   |                        "time",
                                   |                        "value"
                                   |                    ],
                                   |                    "values": [
                                   |                        [
                                   |                            "2015-01-29T21:55:43.702900257Z",
                                   |                            2
                                   |                        ],
                                   |                        [
                                   |                            "2015-01-29T21:55:43.702900257Z",
                                   |                            0.55
                                   |                        ],
                                   |                        [
                                   |                            "2015-06-11T20:46:02Z",
                                   |                            0.64
                                   |                        ]
                                   |                    ]
                                   |                }
                                   |            ]
                                   |        },
                                   |        {
                                   |            "statement_id": 1,
                                   |            "series": [
                                   |                {
                                   |                    "name": "cpu_load_short",
                                   |                    "columns": [
                                   |                        "time",
                                   |                        "count"
                                   |                    ],
                                   |                    "values": [
                                   |                        [
                                   |                            "1970-01-01T00:00:00Z",
                                   |                            3
                                   |                        ]
                                   |                    ]
                                   |                }
                                   |            ]
                                   |        }
                                   |    ]
                                   |}""".stripMargin)
    }

    @TearDown
    def close() = {}
  }

  @State(Scope.Benchmark)
  class GroupedSystem {
    var json: JValue = _

    @Setup
    def up() = {
      json = JParser.parseUnsafe("""{
                                   |    "results": [
                                   |        {
                                   |            "statement_id": 0,
                                   |            "series": [
                                   |                {
                                   |                    "name": "cpu_load_short",
                                   |                    "columns": [
                                   |                        "time",
                                   |                        "value"
                                   |                    ],
                                   |                    "values": [
                                   |                        [
                                   |                            "2015-01-29T21:55:43.702900257Z",
                                   |                            2
                                   |                        ],
                                   |                        [
                                   |                            "2015-01-29T21:55:43.702900257Z",
                                   |                            0.55
                                   |                        ],
                                   |                        [
                                   |                            "2015-06-11T20:46:02Z",
                                   |                            0.64
                                   |                        ]
                                   |                    ]
                                   |                }
                                   |            ]
                                   |        }
                                   |    ]
                                   |}""".stripMargin)
    }

    @TearDown
    def close() = {}
  }

  // query
  final def queryResult(js: JValue): Option[Array[JArray]] =
    js.firstResult.flatMap { json =>
      json.firstSeries
        .flatMap(_.valuesArray)
        .map(_.flatMap[JArray](_.array))
    }

  // grouped
  final def groupedResult(js: JValue): Option[Array[(Array[String], JArray)]] =
    js.firstResult
      .flatMap(_.seriesArray)
      .map(_.flatMap[JObject](_.obj))
      .map { arr =>
        arr.flatMap[(Array[String], JArray)] { obj =>
          val tags   = obj.tags.obj.map(_.vs.values.map(_.asString).toArray.sorted)
          val values = obj.firstValue.flatMap(_.array)

          for {
            tg <- tags
            vl <- values
          } yield tg -> vl
        }
      }

  // bulk
  final def bulkResult(js: JValue): Option[Array[Array[JArray]]] = {
    js.resultsArray
      .map(_.flatMap[JValue](_.firstSeries))
      .map(_.flatMap[Array[JValue]](_.valuesArray))
      .map(_.map(_.flatMap[JArray](_.array)))
  }

  // grouped system
  final def groupedSystemInfoJs(js: JValue): Option[Array[(String, Array[JArray])]] = {
    js.firstResult
      .flatMap(_.seriesArray)
      .map(_.flatMap[JObject](_.obj))
      .map { arr =>
        arr.flatMap[(String, Array[JArray])] { obj =>
          val measurement = obj.get("name").asString
          val cqInfo      = obj.valuesArray.map(_.flatMap[JArray](_.array))

          cqInfo.map(measurement -> _)
        }
      }
  }
}

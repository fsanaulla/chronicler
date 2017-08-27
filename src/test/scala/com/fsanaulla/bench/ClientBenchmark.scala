package com.fsanaulla.bench

import com.fsanaulla.InfluxClientsFactory
import com.fsanaulla.clients.InfluxHttpClient
import com.fsanaulla.utils.Synchronization._
import org.scalameter.api._
import org.scalameter.picklers.Implicits._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 26.08.17
  */
object ClientBenchmark extends Bench[Double] {

  implicit val timeout: FiniteDuration = 5 seconds

  private var influx: InfluxHttpClient = _

  /* configuration */

  lazy val executor = LocalExecutor(
    new Executor.Warmer.Default,
    Aggregator.min[Double],
    measurer)

  lazy val measurer = new Measurer.Default

  lazy val reporter = new LoggingReporter[Double]

  lazy val persistor = Persistor.None

  /* inputs */

  val ranges = for {
    size <- Gen.range("size")(300000, 1500000, 300000)
  } yield 0 until size

  measure method "PING" in {
    using(ranges) config {
      exec.maxWarmupRuns -> 3
      exec.benchRuns -> 3
      exec.independentSamples -> 3
    } setUp { _ =>
      influx = InfluxClientsFactory.createHttpClient("localhost")
    } tearDown { _ =>
      influx.close()
    } in {
      _ => influx.ping().sync
    }
  }

  measure method "showDatabases" in {
    using(ranges) config {
      exec.maxWarmupRuns -> 3
      exec.benchRuns -> 3
      exec.independentSamples -> 3
    } setUp { _ =>
      influx = InfluxClientsFactory.createHttpClient("localhost")
      influx.createDatabase("db").sync
    } tearDown { _ =>
      influx.dropDatabase("db").sync
      influx.close()
    } in {
      _ => influx.showDatabases().sync
    }
  }

}

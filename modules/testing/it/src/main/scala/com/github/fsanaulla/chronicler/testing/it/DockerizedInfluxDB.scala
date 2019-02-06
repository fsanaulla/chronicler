package com.github.fsanaulla.chronicler.testing.it

import com.github.fsanaulla.chronicler.core.model.InfluxCredentials
import org.scalatest.{BeforeAndAfterAll, Suite}
import org.testcontainers.containers.InfluxDBContainer

trait DockerizedInfluxDB extends BeforeAndAfterAll { self: Suite =>

  private val influx: InfluxDBContainer[Nothing] =
    new InfluxDBContainer(sys.env.getOrElse("INFLUXDB_VERSION", "1.7.3"))

  /** Credentials for influx */
  final val creds: InfluxCredentials = InfluxCredentials("admin", "password")

  /** host address */
  def host: String = influx.getContainerIpAddress

  /** mapped port */
  def port: Int = influx.getLivenessCheckPortNumbers.toArray.head.asInstanceOf[Int]

  def mappedPort(mappedPort: Int) = influx.getMappedPort(mappedPort)

  def beforeStartContainer(container: InfluxDBContainer[Nothing]): InfluxDBContainer[Nothing] = container

  override def beforeAll(): Unit = {
    super.beforeAll()
    beforeStartContainer(influx).start()
  }

  override def afterAll(): Unit = {
    super.afterAll()
    influx.stop()
  }
}

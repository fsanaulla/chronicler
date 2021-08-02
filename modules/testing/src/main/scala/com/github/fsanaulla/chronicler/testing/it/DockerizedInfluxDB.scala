package com.github.fsanaulla.chronicler.testing.it

import com.dimafeng.testcontainers.{ForAllTestContainer, GenericContainer}
import com.github.fsanaulla.chronicler.core.model.InfluxCredentials
import org.scalatest.Suite
import org.testcontainers.containers.output.OutputFrame.OutputType
import org.testcontainers.containers.output.ToStringConsumer
import org.testcontainers.containers.wait.strategy.Wait

trait DockerizedInfluxDB extends ForAllTestContainer { self: Suite =>
  private val influxPort        = 8086
  protected def version: String = sys.env.getOrElse("INFLUXDB_VERSION", "1.7.3")

  protected val container: GenericContainer =
    GenericContainer(
      s"influxdb:$version",
      exposedPorts = Seq(influxPort),
      waitStrategy = Wait.forHttp("/")
    )

  /** Credentials for influx */
  final val credentials: InfluxCredentials = InfluxCredentials("admin", "password")

  /** host address */
  def host: String = container.container.getContainerIpAddress

  /** mapped port */
  def port: Int = container.container.getMappedPort(influxPort)

  def mappedPort(mappedPort: Int): Integer = container.mappedPort(mappedPort)

  override def afterStart(): Unit = {
    container.configure(_.followOutput(new ToStringConsumer, OutputType.STDOUT))
    super.afterStart()
  }
}

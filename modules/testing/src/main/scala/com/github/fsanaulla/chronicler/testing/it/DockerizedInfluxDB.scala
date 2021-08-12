package com.github.fsanaulla.chronicler.testing.it

import com.dimafeng.testcontainers.{ForAllTestContainer, GenericContainer}
import com.github.fsanaulla.chronicler.core.model.InfluxCredentials
import org.scalatest.Suite
import org.testcontainers.containers.output.OutputFrame.OutputType
import org.testcontainers.containers.output.ToStringConsumer
import org.testcontainers.containers.wait.strategy.Wait

trait DockerizedInfluxDB extends ForAllTestContainer { self: Suite =>
  def adminName = "admin"
  def adminPassword = "password"
  def influxPort        = 8086
  def version: String = sys.env.getOrElse("INFLUXDB_VERSION", "1.7.3")

  override val container: GenericContainer =
    GenericContainer(
      s"influxdb:$version",
      exposedPorts = Seq(influxPort),
      waitStrategy = Wait.forHttp("/ping").forStatusCode(204),
      env = Map(
        "INFLUXDB_ADMIN_USER"     -> adminName,
        "INFLUXDB_ADMIN_PASSWORD" -> adminPassword,
        "INFLUXDB_HTTP_AUTH_ENABLED" -> String.valueOf(true)
      )
    )

  /** Credentials for influx */
  final val credentials: InfluxCredentials = InfluxCredentials(adminName, adminPassword)

  /** host address */
  def host: String = container.container.getContainerIpAddress

  /** mapped port */
  def port: Int = container.container.getMappedPort(influxPort)

  override def afterStart(): Unit = {
    container.configure(_.followOutput(new ToStringConsumer, OutputType.STDOUT))
    super.afterStart()
  }
}

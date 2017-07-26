package com.fsanaulla.integration

import com.whisk.docker.{DockerContainer, DockerKit}

import scala.concurrent.duration._

/**
  * Created by fayaz on 06.07.17.
  */
trait DockerInfluxService extends DockerKit {

  override val StartContainersTimeout: FiniteDuration = 20 second

  val dockerImage = "influxdb:1.2.4"

  val influxdbContainer: DockerContainer = DockerContainer(dockerImage)
    .withPorts(8086 -> None)

  abstract override def dockerContainers: List[DockerContainer] =
    influxdbContainer :: super.dockerContainers
}

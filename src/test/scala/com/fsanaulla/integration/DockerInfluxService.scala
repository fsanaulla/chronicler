package com.fsanaulla.integration

import com.whisk.docker.{DockerContainer, DockerKit}

import scala.concurrent.duration._

/**
  * Created by fayaz on 06.07.17.
  */
trait DockerInfluxService extends DockerKit {

  override val StartContainersTimeout = 20 second

  val defaultInfluxdbPort = 8086
  val dockerImage = "influxdb:1.2.4"

  val influxdbContainer: DockerContainer = DockerContainer(dockerImage)
    .withPorts(defaultInfluxdbPort -> None)
//    .withReadyChecker(DockerReadyChecker.LogLineContains(s"Waiting for connections on port:$defaultInfluxdbPort"))

  abstract override def dockerContainers: List[DockerContainer] =
    influxdbContainer :: super.dockerContainers
}

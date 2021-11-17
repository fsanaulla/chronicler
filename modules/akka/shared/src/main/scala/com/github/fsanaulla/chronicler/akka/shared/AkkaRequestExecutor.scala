package com.github.fsanaulla.chronicler.akka.shared

import com.github.fsanaulla.chronicler.core.components.RequestExecutor
import sttp.capabilities
import sttp.capabilities.akka.AkkaStreams
import sttp.client3.{Identity, SttpBackend}

import scala.concurrent.Future

private[akka] final class AkkaRequestExecutor(
    backend: SttpBackend[Future, AkkaStreams with capabilities.WebSockets]
) extends RequestExecutor[Future, RequestE[Identity], ResponseE] {
  override def execute(req: RequestE[Identity]): Future[ResponseE] =
    backend.send(req)

  def executeStream(req: RequestS[Identity]): Future[ResponseS] = backend.send(req)
}

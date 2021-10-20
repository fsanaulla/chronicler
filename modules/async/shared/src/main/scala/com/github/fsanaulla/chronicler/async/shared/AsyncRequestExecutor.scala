package com.github.fsanaulla.chronicler.async.shared

import com.github.fsanaulla.chronicler.core.components.RequestExecutor
import sttp.client3.{Identity, SttpBackend}

import scala.concurrent.Future

private[async] final class AsyncRequestExecutor(backend: SttpBackend[Future, Any])
    extends RequestExecutor[Future, RequestE[Identity], ResponseE] {
  override def execute(req: RequestE[Identity]): Future[ResponseE] =
    backend.send(req)
}

package com.github.fsanaulla.chronicler.sync.shared

import com.github.fsanaulla.chronicler.core.components.RequestExecutor
import sttp.client3.{Identity, SttpBackend}

import scala.util.Try

final class SyncRequestExecutor(backend: SttpBackend[Try, Any])
    extends RequestExecutor[Try, RequestE[Identity], ResponseE] {

  override def execute(req: RequestE[Identity]): Try[ResponseE] =
    backend.send(req)
}
